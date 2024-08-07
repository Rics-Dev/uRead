package com.example.uread.presentation.bookReader.components.modals

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.uread.data.model.ReaderPreferences
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.preferences.ReadingProgression
import org.readium.r2.shared.ExperimentalReadiumApi
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class, ExperimentalReadiumApi::class)
@Composable
fun ReaderSettings(
    readerPreferences: ReaderPreferences,
    onPreferencesChanged: (ReaderPreferences) -> Unit,
    onDismiss: () -> Unit,
) {
    var updatedPreferences by remember { mutableStateOf(readerPreferences) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    fun updatePreference(update: ReaderPreferences.() -> ReaderPreferences) {
        updatedPreferences = updatedPreferences.update()
        onPreferencesChanged(updatedPreferences)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Scroll Mode
            SettingsSwitch(
                title = "Scroll Mode",
                checked = updatedPreferences.scroll,
                onCheckedChange = { updatePreference { copy(scroll = it) } }
            )

            // Tap Navigation
            SettingsSwitch(
                title = "Tap Navigation",
                checked = updatedPreferences.tapNavigation,
                onCheckedChange = { updatePreference { copy(tapNavigation = it) } }
            )

            //Reading Progression
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Reading Progression", style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    listOf(
                        ReadingProgression.LTR to "Left to Right",
                        ReadingProgression.RTL to "Right to Left",
                    ).forEach { (readingProgression, label) ->
                        FilledTonalButton(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (updatedPreferences.readingProgression == readingProgression) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                                contentColor = if (updatedPreferences.readingProgression == readingProgression) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            ),
                            onClick = {
                                updatePreference { copy(readingProgression = readingProgression) }
                            }
                        ) {
                            Text(text = label)
                        }
                    }
                }
            }

            // Vertical Text
            SettingsSwitch(
                title = "Vertical Text",
                checked = updatedPreferences.verticalText,
                onCheckedChange = { updatePreference { copy(verticalText = it) } }
            )

            // Publisher Styles
            SettingsSwitch(
                title = "Publisher Styles",
                checked = updatedPreferences.publisherStyles,
                onCheckedChange = { updatePreference { copy(publisherStyles = it) } }
            )

            // Text Normalisation
            SettingsSwitch(
                title = "Text Normalization",
                checked = updatedPreferences.textNormalization,
                onCheckedChange = { updatePreference { copy(textNormalization = it) } }
            )
        }
    }
}

@Composable
fun SettingsSwitch(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
}
