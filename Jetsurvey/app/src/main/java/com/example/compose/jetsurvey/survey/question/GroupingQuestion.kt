/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.jetsurvey.survey.question

import android.content.res.Configuration
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.survey.Answer
import com.example.compose.jetsurvey.survey.PossibleAnswer
import com.example.compose.jetsurvey.survey.question.components.DragTarget
import com.example.compose.jetsurvey.survey.question.components.DragContainer
import com.example.compose.jetsurvey.survey.question.components.DropZone
import com.example.compose.jetsurvey.survey.question.components.LocalDragTargetInfo
import com.example.compose.jetsurvey.survey.question.components.OverflowContainer
import com.example.compose.jetsurvey.theme.JetsurveyTheme

const val STARTING_ZONE_BADGE_GROUP = "Placeholder"

@Composable
fun GroupingQuestion(
    possibleAnswer: PossibleAnswer.Grouping,
    answer: Answer.Grouping?,
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
    var groupingAnswer by rememberSaveable {
        mutableStateOf(
            answer?.answerGrouping ?: mapOf()
        )
    }
    val onGroupingAnswer: (Pair<String, List<Badge>>) -> Unit = { it ->
        groupingAnswer = groupingAnswer.plus(it)
    }
    DragContainer(modifier.fillMaxSize()) {
        Column {
            BadgeGroupStartingZone(
                initialBadgeList,
                groupingAnswer,
                onGroupingAnswer,
                onAnswerReady
            )
            badgeGroupList.forEach { badgeGroup ->
                BadgeGroupDropZone(
                    badgeGroup = badgeGroup,
                    groupAnswer = groupingAnswer,
                    onGroupAnswer = onGroupingAnswer,
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
                badges = badgeList,
                contentPadding = PaddingValues(16.dp),
                onBadgeDropped = onItemDropped
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
    val onBadgeDropped: (Badge) -> Unit = {
        badgeList = badgeList.minus(it)
    }

    val state = LocalDragTargetInfo.current
    val onDraggedItemDropped = {
        // TODO check if this is still called multiple times
        state.dataToTransfer?.let {
            if (it is Badge) {
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
                    badges = badgeList,
                    contentPadding = PaddingValues(8.dp),
                    onBadgeDropped = onBadgeDropped,
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
    badges: List<Badge>,
    onBadgeDropped: (Badge) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    OverflowContainer(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        badges.forEach { badge ->
            DraggableBadge(
                badge = badge,
                onBadgeDropped = onBadgeDropped,
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
        Text(
            text = stringResource(R.string.drop_zone_placeholder),
            color = if (isInDropZone) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Composable
private fun DraggableBadge(
    badge: Badge,
    onBadgeDropped: (Badge) -> Unit,
    modifier: Modifier = Modifier
) {
    DragTarget(
        onItemDropped = onBadgeDropped,
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

@Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DraggableBadgePreview() {
    val badgeNameSample = stringArrayResource(R.array.food_items)[0]
    JetsurveyTheme {
        DraggableBadge(badge = Badge(id = 0, text = badgeNameSample), onBadgeDropped = {})
    }
}

@Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BadgeContainerPreview() {
    val badges =
        stringArrayResource(R.array.food_items).mapIndexed { index, item -> Badge(index, item) }

    JetsurveyTheme {
        BadgeContainer(badges = badges, onBadgeDropped = {})
    }
}

@Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BadgeGroupStartingZonePreview() {
    val badges =
        stringArrayResource(R.array.food_items).mapIndexed { index, item -> Badge(index, item) }

    JetsurveyTheme {
        BadgeGroupStartingZone(
            initialBadgeList = badges,
            groupAnswer = mapOf(),
            onGroupAnswer = {},
            onAnswerReady = {}
        )
    }
}

@Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BadgeGroupDropZonePreview() {
    val badgeGroup =
        stringArrayResource(R.array.food_frequency_groups)[0]

    JetsurveyTheme {
        BadgeGroupDropZone(
            badgeGroup = badgeGroup,
            groupAnswer = mapOf(),
            onGroupAnswer = {},
        )
    }
}

class DropZonePreviewParameterProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(
        true,
        false
    )
}

@Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DropZonePlaceholderPreview(
    @PreviewParameter(DropZonePreviewParameterProvider::class) isInDropZone: Boolean
) {
    JetsurveyTheme {
        DropZonePlaceholder(isInDropZone = isInDropZone)
    }
}

@Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GroupingQuestionPreview() {
    JetsurveyTheme {
        GroupingQuestion(
            possibleAnswer = PossibleAnswer.Grouping(
                items = R.array.food_items,
                groups = R.array.food_frequency_groups
            ),
            answer = Answer.Grouping(mapOf()),
            onAnswerReady = {}
        )
    }
}
