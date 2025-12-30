package com.baidaidai.animora.components.StartScreen.list.model

import com.baidaidai.animora.components.animation.demo.singel.*
import com.baidaidai.animora.R
import com.baidaidai.animora.components.animation.demo.animatable
import com.baidaidai.animora.components.animation.demo.infiniteTransition
import com.baidaidai.animora.shared.dataClass.AnimationDatas


val animationList = listOf<AnimationDatas>(
    AnimationDatas(
        id = 0,
        name = "AnimateColorAsState",
        shortInfo = R.string.animateColorAsState_shortInfo,
        details = "markdown/animateColorAsState.md",
        animationFiles = { blueState -> animateColorAsState(blueState) }
    ),
    AnimationDatas(
        id = 1,
        name = "AnimateContentSize",
        shortInfo = R.string.animateContentSize_shortInfo,
        details = "markdown/animateContentSize.md",
        animationFiles = { blueState -> animateContentSize(blueState) }
    ),
    AnimationDatas(
        id = 2,
        name = "AnimateContentVisibility",
        shortInfo = R.string.animateContentVisibility_shortInfo,
        details = "markdown/animateContentVisibility.md",
        animationFiles = { blueState -> animateContentVisibility(blueState) }
    ),
    AnimationDatas(
        id = 3,
        name = "AnimateOpacityAsState",
        shortInfo = R.string.animateOpacityAsState_shortInfo,
        details = "markdown/animateOpacityAsState.md",
        animationFiles = { blueState -> animateOpacityAsState(blueState) }
    ),
    AnimationDatas(
        id = 4,
        name = "shareBorder",
        shortInfo = R.string.shareBorder_shortInfo,
        details = "markdown/shareTransition.md",
        animationFiles = { blueState -> shareTransition(blueState) }
    ),
    AnimationDatas(
        id = 5,
        name = "shareElement",
        shortInfo = R.string.shareElement_shortInfo,
        details = "markdown/shareElement.md",
        animationFiles = { blueState -> sharedElement(blueState) }
    ),
    AnimationDatas(
        id = 6,
        name = "UpdateTransition",
        shortInfo = R.string.updateTransition_shortInfo,
        details = "markdown/updateTransition.md",
        animationFiles = { blueState -> _updateTransition(blueState) }
    ),
    AnimationDatas(
        id = 7,
        name = "Animatable",
        shortInfo = R.string.AnimateTo_shortInfo,
        details = "markdown/animatable.md",
        animationFiles = { blueState -> animatable.AnimateTo(blueState) }
    ),
    AnimationDatas(
        id = 8,
        name = "WithMediumSpringSpec",
        shortInfo = R.string.withMediumSpringSpec_shortInfo,
        details = "markdown/spring.md",
        animationFiles = { blueState -> animatable.withMediumSpringSpec(blueState) }
    ),
    AnimationDatas(
        id = 9,
        name = "WithDIYBezier",
        shortInfo = R.string.withDIYBezier_shortInfo,
        details = "markdown/CubicBezierEasing.md",
        animationFiles = { blueState -> animatable.withDIYBezier(blueState) }
    ),
    AnimationDatas(
        id = 10,
        name = "WithKeyframesSpline",
        shortInfo = R.string.withKeyframesSpline_shortInfo,
        details = "markdown/keyframes.md",
        animationFiles = { blueState -> animatable.withKeyframesSpline(blueState) }
    ),
    AnimationDatas(
        id = 11,
        name = "WithInfinityRepeatable",
        shortInfo = R.string.withInfinityRepeatable_shortInfo,
        details = "markdown/infiniteRepeatable.md",
        animationFiles = { blueState -> animatable.withInfinityRepeatable(blueState) }
    ),
    AnimationDatas(
        id = 12,
        name = "WithSnap",
        shortInfo = R.string.withSnap_shortInfo,
        details = "markdown/snap.md",
        animationFiles = { blueState -> animatable.withSnap(blueState) }
    ),
    AnimationDatas(
        id = 13,
        name = "AnimateColor",
        shortInfo = R.string.animateColor_shortInfo,
        details = "markdown/animateColor.md",
        animationFiles = { infiniteTransition.animateColor() }
    ),
    AnimationDatas(
        id = 14,
        name = "AnimateFloat",
        shortInfo = R.string.animateFloat_shortInfo,
        details = "markdown/animateFloat.md",
        animationFiles = { infiniteTransition.animateFloat() }
    )
)