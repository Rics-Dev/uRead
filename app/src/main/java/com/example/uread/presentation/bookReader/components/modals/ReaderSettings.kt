package com.example.uread.presentation.bookReader.components.modals

//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Remove
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import kotlinx.coroutines.launch
//import org.readium.r2.navigator.epub.EpubNavigatorFragment
//import org.readium.r2.navigator.epub.EpubPreferences
//import org.readium.r2.shared.ExperimentalReadiumApi
//import java.util.Locale
//
//@OptIn(ExperimentalMaterial3Api::class, ExperimentalReadiumApi::class)
//@Composable
//fun ReaderSettings(
//    navigatorFragment: EpubNavigatorFragment?,
////    initialPreferences: CustomEpubPreferences,
////    onPreferencesChange: (CustomEpubPreferences) -> Unit,
//    onDismiss: () -> Unit
//) {
////    var currentPreferences by remember { mutableStateOf(initialPreferences) }
//    val coroutineScope = rememberCoroutineScope()
//
//    fun Color.toReadiumColor(): org.readium.r2.navigator.preferences.Color {
//        val colorInt = (this.alpha * 255).toInt() shl 24 or
//                (this.red * 255).toInt() shl 16 or
//                (this.green * 255).toInt() shl 8 or
//                (this.blue * 255).toInt()
//        return org.readium.r2.navigator.preferences.Color(colorInt)
//    }
//
//    fun determineTextColor(backgroundColor: Color): Color {
//        val luminance = (0.2126 * backgroundColor.red + 0.7152 * backgroundColor.green + 0.0722 * backgroundColor.blue)
//        return if (luminance < 0.5) Color.White else Color.Black
//    }
//
//
////    val textColor = determineTextColor(currentPreferences.backgroundColor)
//
//
//    fun updatePreferences() {
//        onPreferencesChange(currentPreferences)
//        val newPreferences = EpubPreferences(
//            fontSize = currentPreferences.fontSize,
//            fontWeight = currentPreferences.fontWeight,
//            pageMargins = currentPreferences.pageMargins,
//            scroll = currentPreferences.scroll,
//            backgroundColor = currentPreferences.backgroundColor.toReadiumColor(),
//            textColor = textColor.toReadiumColor()
//        )
//        navigatorFragment?.let { navigator ->
//            coroutineScope.launch {
//                navigator.submitPreferences(newPreferences)
//            }
//        }
//    }
//
//
//
//    ModalBottomSheet(
//        onDismissRequest = onDismiss
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // Font Size
//            Text(
//                "Font Size",
//                style = MaterialTheme.typography.titleMedium,
//                textAlign = TextAlign.Center
//            )
//            Spacer(modifier = Modifier.height(10.dp))
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceAround,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                IconButton(onClick = {
//                    currentPreferences = currentPreferences.copy(
//                        fontSize = (currentPreferences.fontSize - 0.1).coerceAtLeast(0.5)
//                    )
//                    updatePreferences()
//                }) {
//                    Icon(Icons.Default.Remove, contentDescription = "Decrease font size")
//                }
//                Text("${(currentPreferences.fontSize * 100).toInt()}%", style = MaterialTheme.typography.bodyLarge)
//                IconButton(onClick = {
//                    currentPreferences = currentPreferences.copy(
//                        fontSize = (currentPreferences.fontSize + 0.1).coerceAtMost(2.0)
//                    )
//                    updatePreferences()
//                }) {
//                    Icon(Icons.Default.Add, contentDescription = "Increase font size")
//                }
//            }
//
//            Spacer(modifier = Modifier.height(10.dp))
//            HorizontalDivider()
//            Spacer(modifier = Modifier.height(10.dp))
//
//
//            Text(
//                "Page Margins",
//                style = MaterialTheme.typography.titleMedium,
//                textAlign = TextAlign.Center
//            )
//            Spacer(modifier = Modifier.height(10.dp))
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceAround,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                IconButton(onClick = {
//                    currentPreferences = currentPreferences.copy(
//                        pageMargins = (currentPreferences.pageMargins - 0.1).coerceAtLeast(0.5)
//                    )
//                    updatePreferences()
//                }) {
//                    Icon(Icons.Default.Remove, contentDescription = "Decrease page margins")
//                }
//                Text(
//                    String.format(Locale.getDefault(), "%.1f", currentPreferences.pageMargins),
//                    style = MaterialTheme.typography.bodyLarge
//                )
//                IconButton(onClick = {
//                    currentPreferences = currentPreferences.copy(
//                        pageMargins = (currentPreferences.pageMargins + 0.1).coerceAtMost(2.0)
//                    )
//                    updatePreferences()
//                }) {
//                    Icon(Icons.Default.Add, contentDescription = "Increase page margins")
//                }
//            }
//
//            Spacer(modifier = Modifier.height(10.dp))
//            HorizontalDivider()
//            Spacer(modifier = Modifier.height(10.dp))
//
//            // Scroll Mode
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text("Scroll Mode", style = MaterialTheme.typography.titleMedium)
//                Switch(
//                    checked = currentPreferences.scroll,
//                    onCheckedChange = {
//                        currentPreferences = currentPreferences.copy(scroll = it)
//                        updatePreferences()
//                    }
//                )
//            }
//
//            Spacer(modifier = Modifier.height(10.dp))
//            HorizontalDivider()
//            Spacer(modifier = Modifier.height(10.dp))
//
//            // Background Color
//            Text(
//                "Background Color",
//                style = MaterialTheme.typography.titleMedium,
//                textAlign = TextAlign.Center
//            )
//            Spacer(modifier = Modifier.height(10.dp))
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceAround
//            ) {
//                listOf(Color.White, Color.Black, Color.LightGray, Color(0xFFFFF8DC)).forEach { color ->
//                    Box(
//                        modifier = Modifier
//                            .size(40.dp)
//                            .background(color)
//                            .clickable {
//                                currentPreferences = currentPreferences.copy(backgroundColor = color)
//                                updatePreferences()
//                            }
//                    )
//                }
//            }
//        }
//    }
//}