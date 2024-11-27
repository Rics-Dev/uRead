package com.ricdev.uread.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ricdev.uread.R
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.model.FileType
import com.ricdev.uread.data.model.ReadingStatus
import com.ricdev.uread.data.model.SortOption
import com.ricdev.uread.data.model.SortOrder
import com.ricdev.uread.presentation.home.HomeViewModel
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortFilterModal(
    appPreferences: AppPreferences,
    viewModel: HomeViewModel,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableIntStateOf(0) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TabRow(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                selectedTabIndex = selectedTab
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(stringResource(R.string.sort)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(stringResource(R.string.filter)) }
                )
            }

            AnimatedVisibility(
                visible = selectedTab == 0,
            ) {
                SortContent(appPreferences, viewModel)
            }
            AnimatedVisibility(
                visible = selectedTab == 1,
            ) {
                FilterContent(appPreferences, viewModel)
            }
        }
    }
}

@Composable
fun FilterContent(
    appPreferences: AppPreferences,
    viewModel: HomeViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                stringResource(R.string.reading_status),
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                listOf(
                    ReadingStatus.NOT_STARTED to stringResource(R.string.not_started),
                    ReadingStatus.IN_PROGRESS to stringResource(R.string.in_progress),
                    ReadingStatus.FINISHED to stringResource(R.string.finished),
                ).forEach { (readingStatus, label) ->
                    val isSelected = readingStatus in appPreferences.readingStatus

                    ElevatedButton(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            contentColor = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        ),
                        onClick = {
                            viewModel.filterBooks(readingStatus)
                        }
                    ) {
                        Text(text = label)
                    }
                }
            }


            Spacer(Modifier.height(8.dp))


            Text(stringResource(R.string.file_type), style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                listOf(
                    FileType.EPUB to "EPUB",
                    FileType.PDF to "PDF",
                ).forEach { (fileType, label) ->
                    val isSelected = fileType in appPreferences.fileTypes

                    ElevatedButton(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            contentColor = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        ),
                        onClick = {
                            viewModel.filterBooks(fileType)
                        }
                    ) {
                        Text(text = label)
                    }
                }
            }


        }
    }
}


@Composable
fun SortContent(
    appPreferences: AppPreferences,
    viewModel: HomeViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                listOf(
                    SortOrder.ASCENDING to Icons.Default.ArrowUpward,
                    SortOrder.DESCENDING to Icons.Default.ArrowDownward,
                ).forEach { (sortOrder, icon) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = sortOrder.name.lowercase(Locale.getDefault())
                                .replaceFirstChar { it.uppercase(Locale.getDefault()) },
                            style = MaterialTheme.typography.labelMedium,
                        )
                        ElevatedButton(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (appPreferences.sortOrder == sortOrder) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surface
                                },
                                contentColor = if (appPreferences.sortOrder == sortOrder) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            ),
                            onClick = {
                                viewModel.updateAppPreferences(appPreferences.copy(sortOrder = sortOrder))
                                viewModel.sortBooks(appPreferences.sortBy, sortOrder)
                            }
                        ) {
                            Icon(imageVector = icon, contentDescription = "Sort order")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }


        item {

            SortListItem(
                text = stringResource(R.string.last_added),
                selected = appPreferences.sortBy == SortOption.LAST_ADDED,
                onClick = {
                    viewModel.updateAppPreferences(appPreferences.copy(sortBy = SortOption.LAST_ADDED))
                    viewModel.sortBooks(SortOption.LAST_ADDED, appPreferences.sortOrder)
                }
            )
        }

        item {

            SortListItem(
                text = stringResource(R.string.last_opened),
                selected = appPreferences.sortBy == SortOption.LAST_OPENED,
                onClick = {
                    viewModel.updateAppPreferences(appPreferences.copy(sortBy = SortOption.LAST_OPENED))
                    viewModel.sortBooks(SortOption.LAST_OPENED, appPreferences.sortOrder)
                }
            )
        }

        item {
            SortListItem(
                text = stringResource(R.string.title),
                selected = appPreferences.sortBy == SortOption.TITLE,
                onClick = {
                    viewModel.updateAppPreferences(appPreferences.copy(sortBy = SortOption.TITLE))
                    viewModel.sortBooks(SortOption.TITLE, appPreferences.sortOrder)
                }
            )
        }



        item {

            SortListItem(
                text = stringResource(R.string.author),
                selected = appPreferences.sortBy == SortOption.AUTHOR,
                onClick = {
                    viewModel.updateAppPreferences(appPreferences.copy(sortBy = SortOption.AUTHOR))
                    viewModel.sortBooks(SortOption.AUTHOR, appPreferences.sortOrder)
                }
            )
        }




        item {

            SortListItem(
                text = stringResource(R.string.rating),
                selected = appPreferences.sortBy == SortOption.RATING,
                onClick = {
                    viewModel.updateAppPreferences(appPreferences.copy(sortBy = SortOption.RATING))
                    viewModel.sortBooks(SortOption.RATING, appPreferences.sortOrder)
                }
            )
        }


        item {
            SortListItem(
                text = stringResource(R.string.progression),
                selected = appPreferences.sortBy == SortOption.PROGRESSION,
                onClick = {
                    viewModel.updateAppPreferences(appPreferences.copy(sortBy = SortOption.PROGRESSION))
                    viewModel.sortBooks(SortOption.PROGRESSION, appPreferences.sortOrder)
                }
            )
        }

    }
}


@Composable
fun SortListItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .padding(vertical = 4.dp)
            .selectable(selected = selected, onClick = onClick),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
