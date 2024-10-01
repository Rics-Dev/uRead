package com.ricdev.uread.presentation.bookReader.components.drawers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ricdev.uread.R
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.model.Bookmark
import com.ricdev.uread.presentation.bookReader.BookReaderViewModel
//import com.ricsdev.uread.presentation.sharedComponents.PremiumModal
import com.ricdev.uread.util.PurchaseHelper
import org.json.JSONObject
import org.readium.r2.shared.publication.Locator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BookmarksDrawer(
    viewModel: BookReaderViewModel,
    purchaseHelper: PurchaseHelper,
    appPreferences: AppPreferences,
    isOpen: Boolean,
    onClose: () -> Unit,
    bookmarks: List<Bookmark>,
    onBookmarkClick: (Bookmark) -> Unit,
    onRemoveBookmark: (Bookmark) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf(stringResource(R.string.bookmarks))
//    var showPremiumModal by remember { mutableStateOf(false) }


    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it })
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            ModalDrawerSheet(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.Close, contentDescription = "Close Bookmarks")
                        }
                        Text(
                            stringResource(R.string.bookmarks),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    TabRow(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        selectedTabIndex = selectedTabIndex,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(title) }
                            )
                        }
                    }


                    when (selectedTabIndex) {
                        0 -> {
                            BookmarksList(
                                appPreferences = appPreferences,
                                bookmarks = bookmarks.reversed(),
                                onBookmarkClick = onBookmarkClick,
                                onRemoveBookmark = onRemoveBookmark,
                                showPremiumModal = {
//                                    showPremiumModal = true
                                    viewModel.purchasePremium(purchaseHelper)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

//    if (showPremiumModal) {
//        PremiumModal(
//            purchaseHelper = purchaseHelper,
//            hidePremiumModal = { showPremiumModal = false }
//        )
//    }
}

@Composable
fun BookmarksList(
    appPreferences: AppPreferences,
    bookmarks: List<Bookmark>,
    onBookmarkClick: (Bookmark) -> Unit,
    onRemoveBookmark: (Bookmark) -> Unit,
    showPremiumModal: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        items(bookmarks) { bookmark ->
            BookmarkItem(
                appPreferences = appPreferences,
                bookmark = bookmark,
                onClick = { onBookmarkClick(bookmark) },
                onRemoveBookmark = { onRemoveBookmark(bookmark) },
                showPremiumModal = { showPremiumModal() }
            )
            Spacer(modifier = Modifier.height(12.dp))

        }
    }
}

@Composable
fun BookmarkItem(
    appPreferences: AppPreferences,
    bookmark: Bookmark,
    onClick: () -> Unit,
    onRemoveBookmark: (Bookmark) -> Unit,
    showPremiumModal: () -> Unit,
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val formattedDate = remember(bookmark.dateAndTime) { dateFormat.format(Date(bookmark.dateAndTime)) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(8.dp),
                clip = true
            )
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = Locator.fromJSON(JSONObject(bookmark.locator))?.title ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f) // Allocate remaining space to text
                )
                IconButton(
                    onClick = { onRemoveBookmark(bookmark) },
                    modifier = Modifier.size(24.dp) // Adjust size if needed
                ) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Remove Note")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            val progression = Locator.fromJSON(JSONObject(bookmark.locator))?.locations?.totalProgression ?: 0.0
            val progressionPercentage = String.format(Locale.getDefault(),"%.1f%%", progression * 100)
            Text(
                text = "Progression: $progressionPercentage",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Date: $formattedDate",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}