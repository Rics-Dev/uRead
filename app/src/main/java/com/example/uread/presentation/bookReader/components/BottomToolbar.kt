package com.example.uread.presentation.bookReader.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Publication
import java.util.Locale

@OptIn(ExperimentalReadiumApi::class)
@Composable
fun BottomToolbar(
    showToolbar: Boolean,
    progression: Double,
    currentChapter: String,
    onFontSizeChange: (EpubPreferences) -> Unit,
    currentFontSize: Int,  // Change this to Int
    onPageChange: (Double) -> Unit  // Add this parameter
) {

    AnimatedVisibility(
        visible = showToolbar,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
        modifier = Modifier
            .shadow(4.dp)
            .background(Color.White.copy(alpha = 1f))
            .fillMaxWidth()
            .height(120.dp)

    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)


        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Chapter: $currentChapter", maxLines = 1, overflow = TextOverflow.Ellipsis)
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth(),// Add some padding to the sides if needed
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${(progression * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Slider(
                    value = (progression * 100).toFloat(),
                    onValueChange = { value ->
                        onPageChange((value / 100).toDouble())
                    },
                    valueRange = 0f..100f,
                    steps = 0,
                    colors = SliderDefaults.colors(
                        activeTrackColor = Color.DarkGray,
                        inactiveTrackColor = Color.LightGray
                    ),
                    modifier = Modifier.weight(6f) // Adjust weight to make Slider proportionally larger
                )
                Text(
                    text = "100%",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
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
        }
    }
}


// Current page and chapter info
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text("Page $currentPage of $totalPages")
//                Text("Chapter: $currentChapter", maxLines = 1, overflow = TextOverflow.Ellipsis)
//            }
//navigatorFragment?.goBackward()