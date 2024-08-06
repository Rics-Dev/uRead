package com.example.uread.presentation.bookReader.components.toolbars

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ChromeReaderMode
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material.icons.automirrored.sharp.ArrowForward
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.shared.ExperimentalReadiumApi
import androidx.compose.runtime.setValue


@OptIn(ExperimentalReadiumApi::class)
@Composable
fun BottomToolbar(
    navigatorFragment: EpubNavigatorFragment?,
    showToolbar: Boolean,
    progression: Double,
    onPageChange: (Double) -> Unit,  // Add this parameter
    onToggleFontSettings: () -> Unit,
    onToggleUISettings: () -> Unit,
    onToggleReaderSettings: () -> Unit
) {

    var sliderPosition by remember(progression) { mutableDoubleStateOf(progression) }


    AnimatedVisibility(
        visible = showToolbar,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(
                    onClick = { navigatorFragment?.goBackward() },
                    modifier = Modifier
                        .size(40.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(50.dp))
                        .background(Color.White.copy(alpha = 1f), RoundedCornerShape(50.dp))
                        .padding(0.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Sharp.ArrowBack,
                        contentDescription = "Back",
                    )
                }

                Box(
                    modifier = Modifier
                        .height(46.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(50.dp))
                        .background(Color.White.copy(alpha = 0.95f), RoundedCornerShape(50.dp))
                        .border(
                            width = 1.dp,
                            color = Color.Transparent,
                            shape = RoundedCornerShape(50.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 0.dp)
                        .weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${(sliderPosition * 100).toInt()}%",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Slider(
                            value = sliderPosition.toFloat(),
                            onValueChange = { newValue ->
                                sliderPosition = newValue.toDouble()
                            },
                            onValueChangeFinished = {
                                onPageChange(sliderPosition)
                            },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                activeTrackColor = Color.DarkGray,
                                inactiveTrackColor = Color.LightGray
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        )
                        Text(
                            text = "100%",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                IconButton(
                    onClick = { navigatorFragment?.goForward() },
                    modifier = Modifier
                        .size(40.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(50.dp))
                        .background(Color.White.copy(alpha = 1f), RoundedCornerShape(50.dp))
                        .padding(0.dp)

                ) {
                    Icon(Icons.AutoMirrored.Sharp.ArrowForward, contentDescription = "Forward")
                }
            }


            // Buttons Row
            Row(
                modifier = Modifier
                    .padding(top = 5.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = true
                    )
                    .offset(y = (15).dp)
                    .padding(bottom = 15.dp)
                    .background(Color.White.copy(alpha = 1f))
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onToggleReaderSettings() }) {
                    Icon(Icons.AutoMirrored.Outlined.ChromeReaderMode, contentDescription = "Reader Settings", modifier = Modifier.size(28.dp))
                }
                IconButton(onClick = { onToggleUISettings() }) {
                    Icon(Icons.Filled.SettingsBrightness, contentDescription = "UI Settings", modifier = Modifier.size(28.dp))
                }
                IconButton(onClick = { onToggleFontSettings() }) {
                    Icon(Icons.Filled.TextFormat, contentDescription = "Font Settings", modifier = Modifier.size(28.dp))
                }
            }
        }
    }
}




// Font size buttons
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text("Font Size")
//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    IconButton(
//                        onClick = {
//                            val newSize = (currentFontSize - 10).coerceAtLeast(50)
//                            onFontSizeChange(EpubPreferences(fontSize = newSize / 100.0))
//                        }
//                    ) {
//                        Icon(Icons.Default.Remove, contentDescription = "Decrease font size")
//                    }
//                    Text(
//                        text = "$currentFontSize%",
//                        modifier = Modifier.padding(horizontal = 8.dp)
//                    )
//                    IconButton(
//                        onClick = {
//                            val newSize = (currentFontSize + 10).coerceAtMost(200)
//                            onFontSizeChange(EpubPreferences(fontSize = newSize / 100.0))
//                        }
//                    ) {
//                        Icon(Icons.Default.Add, contentDescription = "Increase font size")
//                    }
//                }
//            }


// Current page and chapter info
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text("Page $currentPage of $totalPages")
//                Text("Chapter: $currentChapter", maxLines = 1, overflow = TextOverflow.Ellipsis)
//            }
//navigatorFragment?.goBackward()



//            Row(
//                modifier = Modifier
//                    .fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.Center
//            ) {
//                Text("Chapter: $currentChapter", maxLines = 1, overflow = TextOverflow.Ellipsis)
//            }