/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.jetsnack.widget.layout

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.jetsnack.R
import com.example.jetsnack.widget.layout.Dimensions.NUM_GRID_CELLS
import com.example.jetsnack.widget.layout.Dimensions.fillItemItemPadding
import com.example.jetsnack.widget.layout.Dimensions.filledItemCornerRadius
import com.example.jetsnack.widget.layout.Dimensions.imageCornerRadius
import com.example.jetsnack.widget.layout.Dimensions.verticalSpacing
import com.example.jetsnack.widget.layout.Dimensions.widgetPadding
import com.example.jetsnack.widget.layout.ImageTextListLayoutSize.Companion.shouldDisplayTrailingIconButton
import com.example.jetsnack.widget.layout.ImageTextListLayoutSize.Companion.showTitleBar
import com.example.jetsnack.widget.layout.ImageTextListLayoutSize.Large
import com.example.jetsnack.widget.layout.ImageTextListLayoutSize.Medium
import com.example.jetsnack.widget.layout.ImageTextListLayoutSize.Small
import com.example.jetsnack.widget.utils.ActionUtils.actionStartDemoActivity
import com.example.jetsnack.widget.utils.LargeWidgetPreview
import com.example.jetsnack.widget.utils.MediumWidgetPreview
import com.example.jetsnack.widget.utils.SmallWidgetPreview

private val CART_ITEMS_KEY = ActionParameters.Key<String>("CART_ITEMS_KEY")

/**
 * A layout focused on presenting a list of text with an image, and an optional icon button. The
 * list is displayed in a [Scaffold] below an app-specific title bar.
 *
 * The layout drops not-so-important details as the size of the widget goes smaller. For instance,
 * in the smallest size it drops the image and the icon button to allow displaying more items at
 * at glance. In an medium size, displays image, text and optionally icon button in a single
 * column list. In the large size, shows items in 2 column grid.
 *
 * In this sample layout, text is the primary focus of the widget and image acts as a supporting
 * content. So, we prefer displaying horizontal [ListItem]s in most displays.
 *
 * The layout serves as an implementation suggestion, but should be customized to fit your
 * product's needs. As you customize the layout, prefer supporting narrower as well as larger
 * widget sizes.
 *
 * Note: When using images as bitmap, you should limit the number of items displayed in widgets.
 *
 * @param title the text to be displayed as title of the widget, e.g. name of your widget or app.
 * @param titleIconRes a tintable icon that represents your app or brand, that can be displayed
 * with the provided [title]. In this sample, we use icon from a drawable resource, but you should
 * use an appropriate icon source for your use case.
 * @param titleBarActionIconRes resource id of a tintable icon that can be displayed as
 * an icon button within the title bar area of the widget. For example, a search icon.
 * @param titleBarActionIconContentDescription description of the [titleBarActionIconRes] button
 * to be used by the accessibility services.
 * @param titleBarAction action to be performed on click of the [titleBarActionIconRes] button.
 * @param items list of items to be displayed in the list; typically includes a short title for
 *              item, a supporting text and an image.
 *
 * @see [ImageTextListItemData] for accepted inputs.
 */
@Composable
fun ImageTextListLayout(
    title: String,
    @DrawableRes titleIconRes: Int,
    @DrawableRes titleBarActionIconRes: Int,
    titleBarActionIconContentDescription: String,
    titleBarAction: Action,
    items: List<ImageTextListItemData>,
    shoppingCartActionIntent: Intent,
) {
    val imageTextListLayoutSize = ImageTextListLayoutSize.fromLocalSize()

    fun titleBar(): @Composable (() -> Unit) = {
        TitleBar(
            startIcon = ImageProvider(titleIconRes),
            title = title.takeIf { imageTextListLayoutSize != Small } ?: "",
            iconColor = GlanceTheme.colors.primary,
            textColor = GlanceTheme.colors.onSurface,
            actions = {
                CircleIconButton(
                    imageProvider = ImageProvider(titleBarActionIconRes),
                    contentDescription = titleBarActionIconContentDescription,
                    contentColor = GlanceTheme.colors.secondary,
                    backgroundColor = null, // transparent
                    onClick = titleBarAction,
                )
            },
        )
    }

    val scaffoldTopPadding = if (showTitleBar()) {
        0.dp
    } else {
        widgetPadding
    }

    Scaffold(
        backgroundColor = GlanceTheme.colors.widgetBackground,
        modifier = GlanceModifier.padding(
            top = scaffoldTopPadding,
            bottom = widgetPadding,
        ),
        titleBar = if (showTitleBar()) {
            titleBar()
        } else {
            null
        },
    ) {
        Content(items, shoppingCartActionIntent)
    }
}

@Composable
private fun Content(items: List<ImageTextListItemData>, shoppingCartActionIntent: Intent) {
    val displayTrailingIconIfPresent = shouldDisplayTrailingIconButton()

    if (items.isEmpty()) {
        EmptyListContent()
    } else {
        when (ImageTextListLayoutSize.fromLocalSize()) {
            Small -> {
                ListView(
                    items = items,
                    displayImage = false,
                    displayTrailingIconIfPresent = displayTrailingIconIfPresent,
                    shoppingCartActionIntent = shoppingCartActionIntent,
                )
            }

            Medium -> {
                ListView(
                    items = items,
                    displayImage = true,
                    displayTrailingIconIfPresent = displayTrailingIconIfPresent,
                    shoppingCartActionIntent = shoppingCartActionIntent,
                )
            }

            Large -> {
                GridView(
                    items = items,
                    displayImage = true,
                    displayTrailingIconIfPresent = displayTrailingIconIfPresent,
                    shoppingCartActionIntent = shoppingCartActionIntent,
                )
            }
        }
    }
}

/**
 * A vertical scrolling list displaying [FilledHorizontalListItem]s. Suitable for
 * [ImageTextListLayoutSize.Small] and [ImageTextListLayoutSize.Large] sizes.
 */
@Composable
private fun ListView(
    items: List<ImageTextListItemData>,
    displayImage: Boolean,
    displayTrailingIconIfPresent: Boolean,
    shoppingCartActionIntent: Intent,
) {
    RoundedScrollingLazyColumn(
        modifier = GlanceModifier.fillMaxSize(),
        items = items,
        verticalItemsSpacing = verticalSpacing,
        itemContentProvider = { item ->
            FilledHorizontalListItem(
                item = item,
                displayImage = displayImage,
                displayTrailingIcon = displayTrailingIconIfPresent,
                modifier = GlanceModifier.fillMaxSize(),
                shoppingCartActionIntent = shoppingCartActionIntent,
            )
        },
    )
}

/**
 * A grid of [FilledHorizontalListItem]s suitable for [ImageTextListLayoutSize.Large] sizes.
 *
 * Supporting the grid display allows large screen users view more information at once.
 */
@Composable
private fun GridView(
    items: List<ImageTextListItemData>,
    displayImage: Boolean,
    displayTrailingIconIfPresent: Boolean,
    shoppingCartActionIntent: Intent,
) {
    RoundedScrollingLazyVerticalGrid(
        gridCells = NUM_GRID_CELLS,
        items = items,
        cellSpacing = verticalSpacing,
        itemContentProvider = { item ->
            FilledHorizontalListItem(
                item = item,
                displayImage = displayImage,
                displayTrailingIcon = displayTrailingIconIfPresent,
                modifier = GlanceModifier.fillMaxSize(),
                shoppingCartActionIntent = shoppingCartActionIntent,
            )
        },
        modifier = GlanceModifier.fillMaxSize(),
    )
}

/**
 * Arranges the texts, the image and the icon button in a horizontal arrangement with a filled
 * container.
 */
@Composable
private fun FilledHorizontalListItem(
    item: ImageTextListItemData,
    displayImage: Boolean,
    displayTrailingIcon: Boolean,
    modifier: GlanceModifier = GlanceModifier,
    shoppingCartActionIntent: Intent,
) {
    @Composable
    fun TitleText() {
        Text(
            text = item.title,
            maxLines = 2,
            style = TextStyles.titleText,
        )
    }

    @Composable
    fun SupportingText() {
        Text(
            text = item.supportingText,
            maxLines = 2,
            style = TextStyles.supportingText,
        )
    }

    @Composable
    fun SupportingImage() {
        item.supportingImage?.let {
            Image(
                provider = ImageProvider(item.supportingImage.toString().toInt()),
                // contentDescription is null because in this sample, it serves merely as a visual; but if
                // it gives additional info to user, you should set the appropriate content description.
                contentDescription = null,
                // Depending on your image content, you may want to select an appropriate ContentScale.
                contentScale = ContentScale.Crop,
                // Fixed size per UX spec
                modifier = modifier.cornerRadius(imageCornerRadius).size(Dimensions.imageSize),
            )
        }
    }

    @Composable
    fun IconButton(intent: Intent = shoppingCartActionIntent.clone() as Intent) {
        if (item.trailingIconButton != null) {
            // Using CircleIconButton allows us to keep the touch target 48x48
            CircleIconButton(
                imageProvider = ImageProvider(item.trailingIconButton),
                backgroundColor = null, // to show transparent background.
                contentDescription = item.trailingIconButtonContentDescription,
                onClick = actionStartActivity(
                    intent,
                    actionParametersOf(
                        CART_ITEMS_KEY to item.snackKeys.joinToString(separator = " "),
                    ),
                ),
            )
        }
    }

    ListItem(
        modifier = modifier
            .padding(fillItemItemPadding)
            .cornerRadius(filledItemCornerRadius)
            .background(GlanceTheme.colors.secondaryContainer),
        headlineContent = { TitleText() },
        supportingContent = { SupportingText() },
        leadingContent = if (displayImage) {
            { SupportingImage() }
        } else {
            null
        },
        trailingContent = if (displayTrailingIcon) {
            { IconButton() }
        } else {
            null
        },
    )
}

/**
 * Holds data fields for a ImageTextListLayout.
 *
 * @param key a unique identifier for a specific item
 * @param title a short text (1-3 words) representing the item
 * @param supportingText a compact text (~50-55 characters) supporting the [title]; this allows
 *                       keeping the title short and glanceable, as well as helps support smaller
 *                       widget sizes.
 * @param supportingImage an image to accompany the textual information displayed in [title]
 *                              and the [supportingText].
 * @param trailingIconButton a tintable icon representing an action that can be performed in context
 *                           of the item; e.g. bookmark icon, save icon, etc.
 * @param trailingIconButtonContentDescription description of the [trailingIconButton] to be used by
 *                                             the accessibility services.
 */
data class ImageTextListItemData(
    val key: String,
    val title: String,
    val supportingText: String,
    @DrawableRes val supportingImage: Int? = null,
    @DrawableRes val trailingIconButton: Int? = null,
    val trailingIconButtonContentDescription: String? = null,
    val snackKeys: List<Int>,
)

/**
 * Reference breakpoints for deciding on widget style to display e.g. list / grid etc.
 *
 * In this layout, only width breakpoints are used to scale the layout.
 */
private enum class ImageTextListLayoutSize(val maxWidth: Dp) {
    // Single column vertical list without images or trailing button in this size.
    Small(maxWidth = 260.dp),

    // Single column horizontal list with images and optional trailing button if exists.
    Medium(maxWidth = 479.dp),

    // 2 Column Grid of horizontal list items. Images are always shown; trailing button is shown if
    // it fits.
    Large(maxWidth = 644.dp),
    ;

    companion object {
        /**
         * Returns the corresponding [ImageTextListLayoutSize] to be considered for the current
         * widget size.
         */
        @Composable
        fun fromLocalSize(): ImageTextListLayoutSize {
            val width = LocalSize.current.width

            return if (width >= Medium.maxWidth) {
                Large
            } else if (width >= Small.maxWidth) {
                Medium
            } else {
                Small
            }
        }

        @Composable
        fun showTitleBar(): Boolean = LocalSize.current.height >= 180.dp

        /**
         * Returns if icon button should be displayed across medium and large sizes based on
         * predefined breakpoints.
         */
        @Composable
        fun shouldDisplayTrailingIconButton(): Boolean {
            val widgetWidth = LocalSize.current.width
            return (widgetWidth in 340.dp..479.dp || widgetWidth > 620.dp)
        }
    }
}

private object TextStyles {
    /**
     * Style for the text displayed as title within each item.
     */
    val titleText: TextStyle
        @Composable get() = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = if (ImageTextListLayoutSize.fromLocalSize() == Small) {
                14.sp // M3 Title Small
            } else {
                16.sp // M3 Title Medium
            },
            color = GlanceTheme.colors.onSurface,
        )

    /**
     * Style for the text displayed as supporting text within each item.
     */
    val supportingText: TextStyle
        @Composable get() =
            TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp, // M3 Label Medium
                color = GlanceTheme.colors.secondary,
            )
}

private object Dimensions {
    /** Number of cells in the grid, when items are displayed as a grid. */
    const val NUM_GRID_CELLS = 2

    /** Padding around the the widget content */
    val widgetPadding = 12.dp

    /** Corner radius for each filled list item. */
    val filledItemCornerRadius = 16.dp

    /** Padding applied to each item in the list. */
    val fillItemItemPadding = 12.dp

    /** Vertical Space between each item in the list. */
    val verticalSpacing = 4.dp

    /** Size in which images should be displayed in the list. */
    val imageSize: Dp = 68.dp

    /** Corner radius for image in each item. */
    val imageCornerRadius = 12.dp
}

/**
 * Preview sizes for the widget covering the width based breakpoints of the image grid layout.
 *
 * This allows verifying updates across multiple breakpoints.
 */
@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 259, heightDp = 200)
@Preview(widthDp = 261, heightDp = 200)
@Preview(widthDp = 480, heightDp = 200)
@Preview(widthDp = 644, heightDp = 200)
private annotation class ImageTextListBreakpointPreviews

/**
 * Previews for the image grid layout with both title and supporting text below the image
 *
 * First we look at the previews at defined breakpoints, tweaking them as necessary. In addition,
 * the previews at standard sizes allows us to quickly verify updates across min / max and common
 * widget sizes without needing to run the app or manually place the widget.
 */
@ImageTextListBreakpointPreviews
@SmallWidgetPreview
@MediumWidgetPreview
@LargeWidgetPreview
@Composable
private fun ImageTextListLayoutPreview() {
    val context = LocalContext.current

    ImageTextListLayout(
        title = context.getString(R.string.widget_title),
        titleIconRes = R.drawable.widget_logo,
        titleBarActionIconRes = R.drawable.add_shopping_cart,
        titleBarActionIconContentDescription = context.getString(
            R.string.shopping_cart_button_label,
        ),
        titleBarAction = actionStartDemoActivity("Title bar action click"),
        items = listOf(
            ImageTextListItemData(
                key = "1",
                snackKeys = listOf(0, 20),
                supportingText = "Some text",
                title = "Some title",
            ),
            ImageTextListItemData(
                key = "1",
                snackKeys = listOf(1, 21),
                supportingText = "Some text",
                title = "Some title",
            ),
            ImageTextListItemData(
                key = "1",
                snackKeys = listOf(2, 22),
                supportingText = "Some text",
                title = "Some title",
            ),
            ImageTextListItemData(
                key = "1",
                snackKeys = listOf(3, 23),
                supportingText = "Some text",
                title = "Some title",
            ),
            ImageTextListItemData(
                key = "1",
                snackKeys = listOf(4, 24),
                supportingText = "Some text",
                title = "Some title",
            ),
        ),
        shoppingCartActionIntent = Intent(),
    )
}
