package com.example.uread.presentation.bookReader.components.drawers

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.readium.r2.shared.publication.Link

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChaptersDrawer(
    isOpen: Boolean,
    currentChapter: String,
    tableOfContents: List<Link>,
    onChapterSelect: (Link) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(initialOffsetX = { -it }),
        exit = slideOutHorizontally(targetOffsetX = { -it })
    ) {
        ModalDrawerSheet(
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Chapters", style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close Chapters")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn {
                    items(tableOfContents) { chapter ->
                        ChapterItem(
                            chapter = chapter,
                            isCurrentChapter = chapter.title == currentChapter,
                            onClick = { onChapterSelect(chapter) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChapterItem(
    chapter: Link,
    isCurrentChapter: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = chapter.title ?: "Untitled Chapter",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = if (isCurrentChapter) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
                color = if (isCurrentChapter) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        },
        modifier = Modifier.clickable(onClick = onClick),
        leadingContent = if (isCurrentChapter) {
            {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Current Chapter",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        } else null
    )
    HorizontalDivider()
}