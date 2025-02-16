package com.ricdev.uread.presentation.pdfReader.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material.icons.automirrored.sharp.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PdfReaderBottomBar(
    pageCount: Int,
    currentPage: Int,
    onPageChange: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 60.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(
                onClick = {
                    if (currentPage > 1) {
                        onPageChange(currentPage - 1)
                    }
                },
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
                        text = currentPage.toString(),
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (pageCount > 1) {
                        Slider(
                            value = currentPage.toFloat(),
                            onValueChange = { newValue ->
                                onPageChange(newValue.toInt())
                            },
                            valueRange = 1f..pageCount.toFloat(),
                            // Remove steps parameter as it's causing issues
                            colors = SliderDefaults.colors(
                                activeTrackColor = Color.DarkGray,
                                inactiveTrackColor = Color.LightGray
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        )
                    }

                    Text(
                        text = pageCount.toString(),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            IconButton(
                onClick = {
                    if (currentPage < pageCount) {
                        onPageChange(currentPage + 1)
                    }
                },
                modifier = Modifier
                    .size(40.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(50.dp))
                    .background(Color.White.copy(alpha = 1f), RoundedCornerShape(50.dp))
                    .padding(0.dp)
            ) {
                Icon(Icons.AutoMirrored.Sharp.ArrowForward, contentDescription = "Forward")
            }
        }
    }
}