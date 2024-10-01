package com.ricdev.uread.presentation.bookReader.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ricdev.uread.R


@Composable
fun TtsPlayer(
    areToolbarsVisible: Boolean,
    isTtsOn: Boolean,
    isTtsPlaying: Boolean,
    speed: Double,
    pitch: Double,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onEnd: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onPitchChange: (Float) -> Unit,
    onSkipToNextUtterance: () -> Unit,
    onSkipToPreviousUtterance: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }
    val heightAnimation by animateFloatAsState(
        targetValue = if (isExpanded && !areToolbarsVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing), label = ""
    )


    var showTtsSettings by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = isTtsOn,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .draggable(
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState { delta ->
                                isExpanded = delta < 0
                                showTtsSettings = false
                            }
                        )
                ) {
                    Column {
                        // Drag handle
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(24.dp)
                        ) {
                            HorizontalDivider(
                                thickness = 4.dp,
                                modifier = Modifier
                                    .width(50.dp)
                                    .align(Alignment.Center)
                                    .clip(MaterialTheme.shapes.extraLarge)
                            )
                        }


                        // Main content
                        AnimatedVisibility(
                            visible = !showTtsSettings
                        ) {
                            MainTtsPlayer(
                                heightAnimation = heightAnimation,
                                onSkipToPreviousUtterance = onSkipToPreviousUtterance,
                                onSkipToNextUtterance = onSkipToNextUtterance,
                                isTtsPlaying = isTtsPlaying,
                                onPlay = onPlay,
                                onPause = onPause,
                                onEnd = onEnd,
                                showTtsSettings = { showTtsSettings = true }
                            )
                        }


                        AnimatedVisibility(
                            visible = showTtsSettings
                        ) {
                            TtsSettings(
                                heightAnimation = heightAnimation,
                                speed = speed,
                                pitch = pitch,
                                onSpeedChange = onSpeedChange,
                                onPitchChange = onPitchChange,
                                onEnd = onEnd,
                                hideTtsSettings = { showTtsSettings = false }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun MainTtsPlayer(
    heightAnimation: Float,
    onSkipToPreviousUtterance: () -> Unit,
    onSkipToNextUtterance: () -> Unit,
    isTtsPlaying: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onEnd: () -> Unit,
    showTtsSettings: () -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp * heightAnimation)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Playback controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {


                ElevatedButton(
                    contentPadding = PaddingValues(0.dp),
                    onClick = onSkipToPreviousUtterance
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SkipPrevious,
                        contentDescription = "Skip backward"
                    )
                }

                ElevatedButton(
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.size(60.dp),
                    onClick = if (isTtsPlaying) onPause else onPlay
                ) {
                    Icon(
                        imageVector = if (isTtsPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = "play / pause",
                        modifier = Modifier.size(38.dp)
                    )
                }

                ElevatedButton(
                    contentPadding = PaddingValues(0.dp),
                    onClick = onSkipToNextUtterance
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SkipNext,
                        contentDescription = "Skip forward"
                    )
                }
            }



            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ElevatedButton(
                    contentPadding = PaddingValues(
                        vertical = 8.dp,
                        horizontal = 16.dp
                    ),
                    onClick = showTtsSettings,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = "Show Tts settings"
                    )
                    Text(
                        stringResource(R.string.settings),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }


                // Stop button
                ElevatedButton(
                    contentPadding = PaddingValues(
                        vertical = 8.dp,
                        horizontal = 16.dp
                    ),
                    onClick = onEnd,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Stop,
                        contentDescription = "Stop TTS"
                    )
                    Text(
                        stringResource(R.string.stop),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun TtsSettings(
    heightAnimation: Float,
    speed: Double,
    pitch: Double,
    onSpeedChange: (Float) -> Unit,
    onPitchChange: (Float) -> Unit,
    onEnd: () -> Unit,
    hideTtsSettings: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp * heightAnimation)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Speed control
            Text(
                text = stringResource(R.string.speed_x, speed.format(2)),
                color = MaterialTheme.colorScheme.onSurface
            )
            Slider(
                value = speed.toFloat(),
                onValueChange = onSpeedChange,
                valueRange = 0.25f..1.75f,
                steps = 5
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Pitch control
            Text(
                text = stringResource(R.string.pitch_x, pitch.format(2)),
                color = MaterialTheme.colorScheme.onSurface
            )
            Slider(
                value = pitch.toFloat(),
                onValueChange = onPitchChange,
                valueRange = 0.25f..1.75f,
                steps = 5
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ElevatedButton(
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.size(40.dp),
                    onClick = hideTtsSettings,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew,
                        contentDescription = "Hide TTS settings",
                        modifier = Modifier.size(24.dp)
                    )
                }
                // Stop button
                ElevatedButton(
                    contentPadding = PaddingValues(
                        vertical = 8.dp,
                        horizontal = 16.dp
                    ),
                    onClick = onEnd,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Stop,
                        contentDescription = "Stop TTS"
                    )
                    Text(
                        text = stringResource(R.string.stop),
                        modifier = Modifier.padding(start = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// Helper function to format Double to 2 decimal places
fun Double.format(digits: Int) = "%.${digits}f".format(this)