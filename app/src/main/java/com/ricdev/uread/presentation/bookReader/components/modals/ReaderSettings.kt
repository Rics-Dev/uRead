package com.ricdev.uread.presentation.bookReader.components.modals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ricdev.uread.R
import com.ricdev.uread.data.model.ReaderPreferences
import com.ricdev.uread.presentation.bookReader.BookReaderViewModel
import org.readium.r2.navigator.preferences.ReadingProgression
import org.readium.r2.shared.ExperimentalReadiumApi


@OptIn(ExperimentalMaterial3Api::class, ExperimentalReadiumApi::class)
@Composable
fun ReaderSettings(
    viewModel: BookReaderViewModel,
    readerPreferences: ReaderPreferences,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {



            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.reader_settings),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                TextButton(
                    onClick = {
                        viewModel.resetReaderPreferences()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)

                ) {
                    Text(
                        text = stringResource(R.string.reset),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))



            // Keep Screen On
            SettingsSwitch(
                title = stringResource(R.string.keep_screen_on),
                checked = readerPreferences.keepScreenOn,
                onCheckedChange = { isKeepScreenOn ->
                    viewModel.updateReaderPreferences(
                        readerPreferences.copy(
                            keepScreenOn = isKeepScreenOn,
                        )
                    )
                }
            )

            // Scroll Mode
            SettingsSwitch(
                title = stringResource(R.string.scroll_mode),
                checked = readerPreferences.scroll,
                onCheckedChange = { isScrollMode ->
                    viewModel.updateReaderPreferences(
                        readerPreferences.copy(
                            scroll = isScrollMode,
                            tapNavigation = if (isScrollMode) false else readerPreferences.tapNavigation
                        )
                    )
                }
            )

            // Tap Navigation
            SettingsSwitch(
                title = stringResource(R.string.tap_navigation),
                checked = readerPreferences.tapNavigation,
                onCheckedChange = { isTapNavigation ->
                    viewModel.updateReaderPreferences(
                        readerPreferences.copy(
                            tapNavigation = isTapNavigation,
                            scroll = if (isTapNavigation) false else readerPreferences.scroll
                        )
                    )
                }
            )

            //Reading Progression
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(stringResource(R.string.reading_progression), style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    listOf(
                        ReadingProgression.LTR to stringResource(R.string.left_to_right),
                        ReadingProgression.RTL to stringResource(R.string.right_to_left),
                    ).forEach { (readingProgression, label) ->
                        FilledTonalButton(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (readerPreferences.readingProgression == readingProgression) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                                contentColor = if (readerPreferences.readingProgression == readingProgression) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            ),
                            onClick = {
                                viewModel.updateReaderPreferences(readerPreferences.copy(readingProgression = readingProgression))
                            }
                        ) {
                            Text(text = label, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // Vertical Text
            SettingsSwitch(
                title = stringResource(R.string.vertical_text),
                checked = readerPreferences.verticalText,
                onCheckedChange = { viewModel.updateReaderPreferences(readerPreferences.copy(verticalText = it)) }
            )

            // Publisher Styles
            SettingsSwitch(
                title = stringResource(R.string.publisher_styles),
                checked = readerPreferences.publisherStyles,
                onCheckedChange = { viewModel.updateReaderPreferences(readerPreferences.copy(publisherStyles = it)) }
            )

            // Text Normalisation
            SettingsSwitch(
                title = stringResource(R.string.text_normalization),
                checked = readerPreferences.textNormalization,
                onCheckedChange = { viewModel.updateReaderPreferences(readerPreferences.copy(textNormalization = it)) }
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
    Spacer(modifier = Modifier.height(8.dp))
}
