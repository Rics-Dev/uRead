package com.example.uread.presentation.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.shared.ExperimentalReadiumApi
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalReadiumApi::class)
@Composable
fun ReaderSettings(
    navigatorFragment: EpubNavigatorFragment?,
    initialFontSize: Int,
    initialPageMargins: Double,
    initialScrollMode: Boolean,
    onFontSizeChange: (Int) -> Unit,
    onPageMarginsChange: (Double) -> Unit,
    onScrollModeChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var currentFontSize by remember { mutableIntStateOf(initialFontSize) }
    var currentPageMargins by remember { mutableStateOf(initialPageMargins) }
    var currentScrollMode by remember { mutableStateOf(initialScrollMode) }
    val coroutineScope = rememberCoroutineScope()

    fun updatePreferences() {
        val newPreferences = EpubPreferences(
            fontSize = currentFontSize / 100.0,
            pageMargins = currentPageMargins,
            scroll = currentScrollMode
        )
        navigatorFragment?.let { navigator ->
            coroutineScope.launch {
                navigator.submitPreferences(newPreferences)
            }
        }
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
                    currentFontSize = (currentFontSize - 10).coerceAtLeast(50)
                    onFontSizeChange(currentFontSize)
                    updatePreferences()
                }) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease font size")
                }
                Text("$currentFontSize%", style = MaterialTheme.typography.bodyLarge)
                IconButton(onClick = {
                    currentFontSize = (currentFontSize + 10).coerceAtMost(200)
                    onFontSizeChange(currentFontSize)
                    updatePreferences()
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
                    currentPageMargins = (currentPageMargins - 0.1).coerceAtLeast(0.5)
                    onPageMarginsChange(currentPageMargins)
                    updatePreferences()
                }) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease page margins")
                }

                Text(
                    String.format(Locale.getDefault(), "%.1f", currentPageMargins),
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = {
                    currentPageMargins = (currentPageMargins + 0.1).coerceAtMost(2.0)
                    onPageMarginsChange(currentPageMargins)
                    updatePreferences()
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
                    checked = currentScrollMode,
                    onCheckedChange = {
                        currentScrollMode = it
                        onScrollModeChange(it)
                        updatePreferences()
                    }
                )
            }
        }
    }
}