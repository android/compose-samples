package com.example.compose.jetsurvey.survey.question

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.survey.Answer
import com.example.compose.jetsurvey.survey.PossibleAnswer
import com.example.compose.jetsurvey.survey.question.components.DropZone
import com.example.compose.jetsurvey.survey.question.components.DragTarget
import com.example.compose.jetsurvey.survey.question.components.Draggable
import com.example.compose.jetsurvey.survey.question.components.LocalDragTargetInfo
import com.example.compose.jetsurvey.survey.question.components.OverflowContainer

const val STARTING_ZONE_BADGE_GROUP = "Placeholder"

@Composable
fun GroupingQuestion(
    possibleAnswer: PossibleAnswer.Group,
    answer: Answer.Group?,
    onAnswerReady: (Map<String, List<Badge>>) -> Unit,
    modifier: Modifier = Modifier
) {
    val initialBadgeList =
        stringArrayResource(possibleAnswer.items).mapIndexed { index, item ->
            Badge(index, item)
        }
    val badgeGroupList =
        stringArrayResource(possibleAnswer.groups)
            // Make sure no group name uses reserved name
            .filter { it != STARTING_ZONE_BADGE_GROUP }
            .distinct()
    var groupAnswer by remember {
        mutableStateOf(
            answer?.answerGrouping ?: mapOf()
        )
    }
    val onGroupAnswer: (Pair<String, List<Badge>>) -> Unit = { it ->
        groupAnswer = groupAnswer.plus(it)
    }
    Draggable(modifier.fillMaxSize()) {
        Column {
            BadgeGroupStartingZone(initialBadgeList, groupAnswer, onGroupAnswer, onAnswerReady)
            badgeGroupList.forEach { badgeGroup ->
                BadgeGroupDropZone(
                    badgeGroup = badgeGroup,
                    groupAnswer = groupAnswer,
                    onGroupAnswer = onGroupAnswer,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        }
    }
}

data class Badge(val id: Int, val text: String)

@Composable
private fun BadgeGroupStartingZone(
    initialBadgeList: List<Badge>,
    groupAnswer: Map<String, List<Badge>>,
    onGroupAnswer: (Pair<String, List<Badge>>) -> Unit,
    onAnswerReady: (Map<String, List<Badge>>) -> Unit,
    modifier: Modifier = Modifier
) {
    val isStartingZoneEmpty by remember(groupAnswer) {
        derivedStateOf { groupAnswer[STARTING_ZONE_BADGE_GROUP]?.isEmpty() == true }
    }
    if (isStartingZoneEmpty) {
        onAnswerReady(groupAnswer)
    }
    var badgeList by remember {
        mutableStateOf(
            groupAnswer[STARTING_ZONE_BADGE_GROUP] ?: initialBadgeList
        )
    }
    val onItemDropped: (Badge) -> Unit = {
        badgeList = badgeList.minus(it)
    }
    LaunchedEffect(badgeList) {
        onGroupAnswer(Pair(STARTING_ZONE_BADGE_GROUP, badgeList))
    }

    DropZone(modifier = modifier, isDropZone = false) { _, _ ->
        if (badgeList.isNotEmpty()) {
            BadgeContainer(
                items = badgeList,
                contentPadding = PaddingValues(16.dp),
                onItemDropped = onItemDropped
            )
        }
    }
}


@Composable
private fun BadgeGroupDropZone(
    badgeGroup: String,
    groupAnswer: Map<String, List<Badge>>,
    onGroupAnswer: (Pair<String, List<Badge>>) -> Unit,
    modifier: Modifier = Modifier
) {
    var badgeList by remember {
        mutableStateOf(
            groupAnswer[badgeGroup] ?: emptyList()
        )
    }
    val onItemDropped: (Badge) -> Unit = {
        badgeList = badgeList.minus(it)
    }

    val state = LocalDragTargetInfo.current
    val onDraggedItemDropped = {
        // TODO check if this is still called multiple times
        state.dataToTransfer?.let {
            if(it is Badge) {
                badgeList = badgeList.plus(it)
            }
            state.dataToTransfer = null
        }
    }

    LaunchedEffect(badgeList) {
        onGroupAnswer(Pair(badgeGroup, badgeList))
    }

    Column(modifier) {
        Text(
            text = badgeGroup,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        DropZone { isDraggingInDropZone, hasDragEndInDropZone ->
            if (hasDragEndInDropZone) {
                onDraggedItemDropped()
            }
            if (badgeList.isEmpty()) {
                DropZonePlaceholder(isDraggingInDropZone)
            } else {
                BadgeContainer(
                    items = badgeList,
                    contentPadding = PaddingValues(8.dp),
                    onItemDropped = onItemDropped,
                    modifier = Modifier
                        .border(
                            shape = MaterialTheme.shapes.small,
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        )
                        .background(
                            color = if (isDraggingInDropZone) {
                                MaterialTheme.colorScheme.surfaceVariant
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun BadgeContainer(
    items: List<Badge>,
    onItemDropped: (Badge) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    OverflowContainer(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        items.forEach { badge ->
            DraggableBadge(
                badge = badge,
                onItemDropped = onItemDropped,
            )
        }
    }
}

@Composable
private fun DropZonePlaceholder(isInDropZone: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(80.dp)
            .border(
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            )
            .background(
                color = if (isInDropZone) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.surface
                },
                shape = MaterialTheme.shapes.small
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(R.string.drop_zone_placeholder))
    }
}

@Composable
private fun DraggableBadge(
    badge: Badge,
    onItemDropped: (Badge) -> Unit,
    modifier: Modifier = Modifier
) {
    DragTarget(
        onItemDropped = onItemDropped,
        item = badge,
        modifier = modifier
    ) { isItemDragged ->
        Surface(
            shape = MaterialTheme.shapes.small,
            color = if (isItemDragged) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            },
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline
            ),
        ) {
            Text(
                text = badge.text,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
