package com.ricdev.uread.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ricdev.uread.R
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.model.Layout
import com.ricdev.uread.presentation.bookReader.components.modals.SettingsSwitch
import com.ricdev.uread.presentation.home.HomeViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayoutModal(
    appPreferences: AppPreferences,
    viewModel: HomeViewModel,
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

            //Layout
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.layout, appPreferences.homeLayout),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                TextButton(
                    onClick = {
                        viewModel.resetLayoutPreferences()
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


            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                listOf(
                    Layout.Grid to stringResource(R.string.grid_layout),
                    Layout.List to stringResource(R.string.list_layout),
                ).forEach { (layout, label) ->
                    FilledTonalButton(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (appPreferences.homeLayout == layout || (appPreferences.homeLayout == Layout.CoverOnly && layout == Layout.Grid)) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            contentColor = if (appPreferences.homeLayout == layout || (appPreferences.homeLayout == Layout.CoverOnly && layout == Layout.Grid)) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        ),
                        onClick = {
                            viewModel.updateAppPreferences(appPreferences.copy(homeLayout = layout))
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (layout == Layout.Grid) {
                                    Icons.Default.GridView
                                } else {
                                    Icons.Outlined.ViewAgenda
                                },
                                contentDescription = null,
                                tint = if (appPreferences.homeLayout == layout || (appPreferences.homeLayout == Layout.CoverOnly && layout == Layout.Grid)) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                            Text(text = label, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ){
               item {

                   AnimatedVisibility(
                       visible = (appPreferences.homeLayout == Layout.Grid || appPreferences.homeLayout == Layout.CoverOnly)
                   ) {
                       Column(
                           horizontalAlignment = Alignment.CenterHorizontally
                       ) {
                           Text(
                               stringResource(R.string.grid_count, appPreferences.gridCount),
                               style = MaterialTheme.typography.titleMedium
                           )
                           Slider(
                               value = appPreferences.gridCount.toFloat(),
                               onValueChange = { newValue ->
                                   val updatedValue = newValue.roundToInt().coerceIn(2, 5)
                                   viewModel.updateAppPreferences(appPreferences.copy(gridCount = updatedValue))
                               },
                               valueRange = 2f..5f,
                               steps = 2,
                           )
                           Spacer(modifier = Modifier.height(8.dp))
                           SettingsSwitch(
                               title = stringResource(R.string.cover_only),
                               checked = appPreferences.homeLayout == Layout.CoverOnly,
                               onCheckedChange = { newValue ->
                                   viewModel.updateAppPreferences(appPreferences.copy(homeLayout = if (newValue) Layout.CoverOnly else Layout.Grid))
                               }
                           )
                       }

                   }
               }





                item {

                    AnimatedVisibility(
                        visible = appPreferences.homeLayout == Layout.List
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            SettingsSwitch(
                                title = stringResource(R.string.show_reading_status),
                                checked = appPreferences.showReadingStatus,
                                onCheckedChange = {
                                    viewModel.updateAppPreferences(
                                        appPreferences.copy(
                                            showReadingStatus = it
                                        )
                                    )
                                }
                            )
                            SettingsSwitch(
                                title = stringResource(R.string.show_reading_dates),
                                checked = appPreferences.showReadingDates,
                                onCheckedChange = {
                                    viewModel.updateAppPreferences(
                                        appPreferences.copy(
                                            showReadingDates = it
                                        )
                                    )
                                }
                            )
                        }


                    }
                }



                item {

                    SettingsSwitch(
                        title = stringResource(R.string.show_ratings),
                        checked = appPreferences.showRating,
                        onCheckedChange = { viewModel.updateAppPreferences(appPreferences.copy(showRating = it)) }
                    )
                }


                item {
                    SettingsSwitch(
                        title = stringResource(R.string.show_pdf_label),
                        checked = appPreferences.showPdfLabel,
                        onCheckedChange = { viewModel.updateAppPreferences(appPreferences.copy(showPdfLabel = it)) }
                    )
                }


                item {
                    SettingsSwitch(
                        title = stringResource(R.string.show_entries),
                        checked = appPreferences.showEntries,
                        onCheckedChange = { viewModel.updateAppPreferences(appPreferences.copy(showEntries = it)) }
                    )
                }
            }

        }
    }
}