package com.ricdev.uread.presentation.bookDetails.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ricdev.uread.R
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.presentation.bookDetails.BookDetailsViewModel
import com.ricdev.uread.presentation.sharedComponents.dialogs.ReadingDatesDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReadingStats(book: Book, viewModel: BookDetailsViewModel) {

    var showReadingDatesDialog by remember { mutableStateOf(false) }
    var isStartDate by remember { mutableStateOf(true) }

    Column {
        Text(
            text = stringResource(R.string.reading_stats),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        stringResource(R.string.total_reading_time),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        formatReadingTime(book.readingTime),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                StatRow(
                    "Started Reading",
                    book.startReadingDate?.let { formatDate(it) } ?: "Not Yet",
                    showReadingDatesDialog = {
                        showReadingDatesDialog = true
                        isStartDate = true
                    }
                )
                StatRow(
                    "Finished Reading",
                    book.endReadingDate?.let { formatDate(it) } ?: "Not Yet",
                    showReadingDatesDialog = {
                        showReadingDatesDialog = true
                        isStartDate = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

    }


    if (showReadingDatesDialog) {
        ReadingDatesDialog(
            initialDate = if (isStartDate) book.startReadingDate else book.endReadingDate,
            onDateSelected = { newDate ->
                if (isStartDate) {
                    viewModel.updateBook(book.copy(startReadingDate = newDate))
                } else {
                    viewModel.updateBook(book.copy(endReadingDate = newDate))
                }
                showReadingDatesDialog = false
            },
            onDismiss = { showReadingDatesDialog = false },
            isStartDate = isStartDate
        )
    }
}


@Composable
fun StatRow(
    label: String,
    value: String,
    showReadingDatesDialog: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (value == "Not Yet") {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .clickable(
                        onClick = {
                            showReadingDatesDialog()
                        }
                    )
            ) {
                Text(
                    text = value,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        } else {
            Text(value, style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .clickable(
                        onClick = {
                            showReadingDatesDialog()
                        }
                    )
            )
        }
    }
}




fun formatReadingTime(timeInMillis: Long): String {
    val hours = timeInMillis / (1000 * 60 * 60)
    val minutes = (timeInMillis % (1000 * 60 * 60)) / (1000 * 60)
    val seconds = (timeInMillis % (1000 * 60)) / 1000
    return "${hours}h ${minutes}m ${seconds}s"
}


fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}