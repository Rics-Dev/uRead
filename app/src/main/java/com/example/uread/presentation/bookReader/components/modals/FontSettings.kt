package com.example.uread.presentation.bookReader.components.modals


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.uread.data.model.ReaderPreferences
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontSettings(
    readerPreferences: ReaderPreferences,
    onPreferencesChanged: (ReaderPreferences) -> Unit,
    onDismiss: () -> Unit,
) {
    var updatedPreferences by remember { mutableStateOf(readerPreferences) }

    fun updatePreference(update: ReaderPreferences.() -> ReaderPreferences) {
        updatedPreferences = updatedPreferences.update()
        onPreferencesChanged(updatedPreferences)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Font Size
            Text(
                "Font Size",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    updatePreference { copy(fontSize = fontSize - 0.1) }
                }) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease font size")
                }
                Text("${(updatedPreferences.fontSize * 100).toInt()}%", style = MaterialTheme.typography.bodyLarge)
                IconButton(onClick = {
                    updatePreference { copy(fontSize = fontSize + 0.1) }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Increase font size")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(10.dp))

            // Page Margins
            Text(
                "Page Margins",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    updatePreference { copy(pageMargins = pageMargins - 0.1) }
                }) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease page margins")
                }
                Text(
                    String.format(Locale.getDefault(), "%.1f", updatedPreferences.pageMargins),
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = {
                    updatePreference { copy(pageMargins = pageMargins + 0.1) }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Increase page margins")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(10.dp))

            // Scroll Mode
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Scroll Mode", style = MaterialTheme.typography.titleMedium)
                Switch(
                    checked = updatedPreferences.scroll,
                    onCheckedChange = {
                        updatePreference { copy(scroll = it) }
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(10.dp))

            // Background Color
            Text(
                "Background Color",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                listOf(Color.White, Color.Black, Color.LightGray, Color(0xFFFFF8DC)).forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(color)
                            .clickable {
                                updatePreference {
                                    copy(
                                        backgroundColor = color,
                                        textColor = if (color == Color.Black) Color.White else Color.Black
                                    )
                                }
                            }
                    )
                }
            }
        }
    }
}