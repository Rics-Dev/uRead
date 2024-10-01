package com.ricdev.uread.presentation.bookDetails.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ricdev.uread.R
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.data.model.ReadingStatus
import com.ricdev.uread.presentation.bookDetails.BookDetailsViewModel
import com.ricdev.uread.presentation.sharedComponents.dialogs.ReadingStatusDialog

@Composable
fun ReadingProgress(
    viewModel: BookDetailsViewModel,
    book: Book
) {


    var showReadingStatusDialog by remember { mutableStateOf(false) }


    Column {

        book.subjects?.takeIf { it.isNotBlank() }?.let { subjectsString ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                subjectsString.split(", ").filter { it.isNotBlank() }.forEach { subject ->
                    SuggestionChip(
                        onClick = {},
                        label = { Text(subject) }
                    )
                }
            }
        }

        if (book.subjects?.isNotBlank() == true) {
            Spacer(modifier = Modifier.height(16.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .clickable(onClick = {
                        showReadingStatusDialog = true
                    }),
                imageVector = when (book.readingStatus) {
                    ReadingStatus.NOT_STARTED -> Icons.Outlined.Book
                    ReadingStatus.IN_PROGRESS -> Icons.Outlined.AutoStories
                    ReadingStatus.FINISHED -> Icons.Outlined.CheckCircle
                    null -> Icons.Outlined.Book
                },
                contentDescription = book.readingStatus?.name,
            )
            Spacer(modifier = Modifier.width(8.dp))
            StatusChip(
                book.readingStatus,
                showReadingStatusDialog = { showReadingStatusDialog = true }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(end = 16.dp),
                text = "${book.progression.toInt()}%",
                style = MaterialTheme.typography.bodyMedium
            )
            LinearProgressIndicator(
                progress = { book.progression / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50.dp)),
            )
        }
    }

    if (showReadingStatusDialog) {
        ReadingStatusDialog(
            currentStatus = book.readingStatus,
            onStatusSelected = { newStatus ->
                viewModel.updateBook(
                    book.copy(readingStatus = newStatus),
                    updatedReadingStatus = true
                )
                showReadingStatusDialog = false
            },
            onDismiss = { showReadingStatusDialog = false }
        )
    }


}



@Composable
fun StatusChip(
    status: ReadingStatus?,
    showReadingStatusDialog: () -> Unit,
) {
    val (backgroundColor, contentColor) = when (status) {
        ReadingStatus.NOT_STARTED -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        ReadingStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        ReadingStatus.FINISHED -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        null -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        contentColor = contentColor,
        modifier = Modifier
            .clickable(
                onClick = {
                    showReadingStatusDialog()
                }
            )
    ) {
        Text(
            text = when (status) {
                ReadingStatus.NOT_STARTED -> stringResource(R.string.not_started)
                ReadingStatus.IN_PROGRESS -> stringResource(R.string.in_progress)
                ReadingStatus.FINISHED -> stringResource(R.string.finished)
                null -> "Unknown"
            },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}