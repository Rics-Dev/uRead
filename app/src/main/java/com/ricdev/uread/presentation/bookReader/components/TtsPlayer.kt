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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ricdev.uread.R
import com.ricdev.uread.data.model.AppLanguage
import org.readium.r2.shared.util.Language


@Composable
fun TtsPlayer(
    areToolbarsVisible: Boolean,
    isTtsOn: Boolean,
    isTtsPlaying: Boolean,
    speed: Double,
    pitch: Double,
    language: Language,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onEnd: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onPitchChange: (Float) -> Unit,
    onLanguageChange: (Language) -> Unit,
    onSkipToNextUtterance: () -> Unit,
    onSkipToPreviousUtterance: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }
    val heightAnimation by animateFloatAsState(
        targetValue = if (isExpanded && !areToolbarsVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing), label = ""
    )


    var showTtsSettings by remember { mutableStateOf(false) }
    var showLanguageSettings by remember { mutableStateOf(false) }

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
                            visible = !showTtsSettings && !showLanguageSettings
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
                                hideTtsSettings = { showTtsSettings = false },
                                showLanguageSettings = {
                                    showTtsSettings = false
                                    showLanguageSettings = true
                                }
                            )
                        }



                        AnimatedVisibility(
                            visible = showLanguageSettings
                        ) {
                            LanguageSettings(
                                heightAnimation = heightAnimation,
                                currentLanguage = language,
                                onLanguageChange = onLanguageChange,
                                onClose = {
                                    showLanguageSettings = false
                                    showTtsSettings = true
                                },
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
    hideTtsSettings: () -> Unit,
    showLanguageSettings: () -> Unit,
) {


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp * heightAnimation)
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
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Slider(
                value = speed.toFloat(),
                onValueChange = onSpeedChange,
                valueRange = 0.25f..1.75f,
                steps = 5,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Pitch control
            Text(
                text = stringResource(R.string.pitch_x, pitch.format(2)),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Slider(
                value = pitch.toFloat(),
                onValueChange = onPitchChange,
                valueRange = 0.25f..1.75f,
                steps = 5,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ElevatedButton(
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.size(48.dp),
                    onClick = hideTtsSettings,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew,
                        contentDescription = "Hide TTS settings",
                        modifier = Modifier.size(24.dp)
                    )
                }
                // language button
                ElevatedButton(
                    contentPadding = PaddingValues(
                        vertical = 8.dp,
                        horizontal = 16.dp
                    ),
                    onClick = showLanguageSettings,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Language,
                        contentDescription = "Change tts language"
                    )
                    Text(
                        text = stringResource(R.string.language),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun LanguageSettings(
    heightAnimation: Float,
    currentLanguage: Language,
    onLanguageChange: (Language) -> Unit,
    onClose: () -> Unit
) {
    val languages = AppLanguage.entries.filter { it != AppLanguage.SYSTEM }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp * heightAnimation)
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                ElevatedButton(
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.size(48.dp),
                    onClick = onClose,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew,
                        contentDescription = "Hide TTS settings",
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .wrapContentSize(Alignment.Center),
                    text = "",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }

            LazyColumn {
                items(languages) { lang ->
                    val isSelected = lang.code == currentLanguage.code
                    ElevatedButton(
                        onClick = {
                            onLanguageChange(Language(lang.code))
                            onClose()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = if (isSelected) {
                            ButtonDefaults.elevatedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            ButtonDefaults.elevatedButtonColors()
                        }
                    ) {
                        Text(text = lang.displayName)
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "selected language",
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


// Helper function to format Double to 2 decimal places
fun Double.format(digits: Int) = "%.${digits}f".format(this)