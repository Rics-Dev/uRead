package com.ricdev.uread.util

import com.elixer.palette.composables.LaunchButton


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.elixer.palette.Presets
import com.elixer.palette.constraints.HorizontalAlignment
import com.elixer.palette.constraints.HorizontalAlignment.*
import com.elixer.palette.constraints.VerticalAlignment
import com.elixer.palette.constraints.VerticalAlignment.*
import com.elixer.palette.Utils.Companion.calculateAngle
import com.elixer.palette.Utils.Companion.calculateDistance
import com.elixer.palette.models.ColorArc
import com.elixer.palette.models.ColorWheel
import com.elixer.palette.models.toColorArch
import com.elixer.palette.models.toSwatches
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.atan2



@Composable
fun  ColorPicker(
    isVisible: Boolean,
    defaultColor: Color = Color(0xFFFF9800),
    buttonSize: Dp = 100.dp,
    swatches: List<List<Color>>,
    innerRadius: Float = 440f,
    strokeWidth: Float = 120f,
    selectorColor: Color = Color.White,
    spacerRotation: Float = 2f,
    spacerOutward: Float = 2f,
    verticalAlignment: VerticalAlignment = Top,
    horizontalAlignment: HorizontalAlignment = Start,
    buttonColorChangeAnimationDuration: Int = 1000,
    selectedArchAnimationDuration: Int = 1000,
    onColorSelected: (Color) -> Unit = {},
    colorWheelZIndexOnWheelDisplayed:Float = 1f,
    colorWheelZIndexOnWheelHidden:Float = 0f
) {

    val isPaletteDisplayed by rememberUpdatedState(isVisible)
    val selectedArchAnimatable = remember { Animatable(0f) }
    val selectedColor = remember { mutableStateOf(defaultColor) }


    // Add this animated value
    val visibilityTransition by animateFloatAsState(
        targetValue = if (isPaletteDisplayed) 1f else 0f,
        animationSpec = tween(durationMillis = 300), label = ""
    )


    val animatedColor by animateColorAsState(
        selectedColor.value,
        tween(
            durationMillis = buttonColorChangeAnimationDuration,
            easing = LinearEasing
        ), label = ""
    )

    var centerX by remember { mutableFloatStateOf(0f) }
    var centerY by remember { mutableFloatStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()

    val selectedArch = remember {
        mutableStateOf(ColorArc(radius = 0f, strokeWidth = 0f, startingAngle = 0f, sweep = 40f, color = selectorColor))
    }

    val colorWheel = ColorWheel(
        startingRadius = innerRadius, swatches = swatches,
        strokeWidth = strokeWidth,
        isDisplayed = isPaletteDisplayed,
        spacerOutward = spacerOutward,
        spacerRotation = spacerRotation
    )

    val wheelSwatches = colorWheel.toSwatches()
    val colorArcs = mutableListOf<ColorArc>()

    wheelSwatches.forEach {
        colorArcs.addAll(it.toColorArch())
    }

    val radiusAnimatables = mutableListOf<Float>()
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var dragStartedAngle by remember { mutableFloatStateOf(0f) }
    var oldAngle by remember { mutableFloatStateOf(rotationAngle) }

//    colorArcs.forEachIndexed { index, it ->
//        val radius: Float by animateFloatAsState(
//            targetValue = if (isPaletteDisplayed) it.radius else 0f,
//            animationSpec = spring(
//                dampingRatio = Spring.DampingRatioLowBouncy,
//                stiffness = Spring.StiffnessVeryLow
//            )
//        )
//        radiusAnimatables.add(radius)
//    }

    colorArcs.forEachIndexed { _, it ->
        val radius: Float by animateFloatAsState(
            targetValue = if (isPaletteDisplayed) it.radius else 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessVeryLow
            ), label = ""
        )
        radiusAnimatables.add(radius * visibilityTransition)
    }

    val rotationAnimatable: Float by animateFloatAsState(
        targetValue = rotationAngle,
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearEasing
        ), label = ""
    )

    fun onColorSelected(colorArc: ColorArc) {
        onColorSelected(colorArc.color)
        selectedArch.value = colorArc
//        isPaletteDisplayed = false
        selectedColor.value = colorArc.color

        coroutineScope.launch {
            selectedArchAnimatable.snapTo(
                colorArc.radius
            )
            selectedArchAnimatable.animateTo(
                targetValue = 0f,
                tween(
                    durationMillis = selectedArchAnimationDuration,
                    easing = LinearEasing
                )
            )
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .zIndex(if (isPaletteDisplayed) colorWheelZIndexOnWheelDisplayed else colorWheelZIndexOnWheelHidden)
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        dragStartedAngle = atan2(
                            y = centerX - offset.x,
                            x = centerY - offset.y
                        ) * (180f / Math.PI.toFloat()) * -1
                    },
                    onDragEnd = {
                        oldAngle = rotationAngle
                    }
                ) { change, _ ->
                    val touchAngle = atan2(
                        y = centerX - change.position.x,
                        x = centerY - change.position.y
                    ) * (180f / Math.PI.toFloat()) * -1

                    rotationAngle = oldAngle + (touchAngle - dragStartedAngle)

                    //make angles positive
                    if (rotationAngle > 360) {
                        rotationAngle -= 360
                    } else if (rotationAngle < 0) {
                        rotationAngle = 360 - abs(rotationAngle)
                    }
                }
            }
    ) {

        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { tapOffset ->
                        if (isPaletteDisplayed) {

                            /**
                             * Calculate angle between center and tapped offset
                             */
                            val angle = calculateAngle(centerX.dp.value, centerY.dp.value, tapOffset.x, tapOffset.y)

                            /**
                             * Calculate distance between center and tapped offset
                             */
                            val distance = calculateDistance(centerX, centerY, tapOffset.x, tapOffset.y)
                            colorArcs.forEachIndexed { _, it ->
                                if (it.contains(angle, distance, rotationAnimatable)) {
                                    onColorSelected(it)
                                    return@forEachIndexed
                                } else {
//                                    isPaletteDisplayed.value = false
                                }
                            }
                        }
                    },
                )
            }
        ) {
            centerX = getCenterXCoordinate(horizontalAlignment, size.width)
            centerY = getCenterYCoordinate(verticalAlignment, size.height)
            colorArcs.forEachIndexed { index, it ->
                val radius = radiusAnimatables[index]
                this.drawColouredArc(it, rotationAnimatable, centerX, radius, centerY)
            }

            drawSelectorArc(selectedArch, selectorColor, rotationAnimatable, centerX, selectedArchAnimatable, centerY)
            drawColouredArc(selectedArch.value, rotationAnimatable, centerX, selectedArchAnimatable.value, centerY)
        }

//        LaunchButton(
//            selectedColor = animatedColor,
////            isPaletteDisplayed.value = !isPaletteDisplayed.value
//            onToggleAnimationState = {  },
//            offsetX = getCenterXCoordinate(horizontalAlignment, maxWidth.value).dp,
//            offsetY = getCenterYCoordinate(verticalAlignment, maxHeight.value).dp,
//            buttonSize = buttonSize
//        )

        LaunchButton(
            selectedColor = animatedColor,
            onToggleAnimationState = { /* You might want to call a callback here to toggle visibility */ },
            offsetX = getCenterXCoordinate(horizontalAlignment, maxWidth.value).dp,
            offsetY = getCenterYCoordinate(verticalAlignment, maxHeight.value).dp,
            buttonSize = buttonSize
        )
    }
}

private fun DrawScope.drawSelectorArc(
    selectedArch: MutableState<ColorArc>,
    selectorColor: Color,
    rotationAnimatable: Float,
    centerX: Float,
    newSelectedAnimatable: Animatable<Float, AnimationVector1D>,
    centerY: Float
) {
    drawArc(
        color = selectorColor,
        startAngle = selectedArch.value.startingAngle + rotationAnimatable - 1f,
        sweepAngle = selectedArch.value.sweep + 2f,
        useCenter = false,
        topLeft = Offset(centerX - newSelectedAnimatable.value, centerY - newSelectedAnimatable.value),
        style = Stroke(width = selectedArch.value.strokeWidth + 20f),
        size = Size(2 * newSelectedAnimatable.value, 2 * newSelectedAnimatable.value)
    )
}

private fun DrawScope.drawColouredArc(
    it: ColorArc,
    rotationAnimatable: Float,
    centerX: Float,
    radius: Float,
    centerY: Float
) {
    drawArc(
        color = it.color,
        startAngle = it.startingAngle + rotationAnimatable,
        sweepAngle = it.sweep,
        useCenter = false,
        topLeft = Offset(centerX - radius, centerY - radius),
        style = Stroke(width = it.strokeWidth),
        size = Size(2 * radius, 2 * radius)
    )
}

fun getCenterXCoordinate(horizontalAxis: HorizontalAlignment, maxX: Float): Float {
    return when (horizontalAxis) {
        is Start -> 0f
        is Center -> maxX / 2
        is End -> maxX
    }
}

fun getCenterYCoordinate(verticalAxis: VerticalAlignment, maxY: Float): Float {
    return when (verticalAxis) {
        is Top -> 0f
        is Middle -> maxY / 2
        is Bottom -> maxY
    }
}

@Preview(showBackground = true, widthDp = 500, heightDp = 900)
@Composable
fun PreviewPalette() {
    ColorPicker(
        isVisible = true,
        defaultColor = Blue,
        swatches = Presets.material(),
    )
}