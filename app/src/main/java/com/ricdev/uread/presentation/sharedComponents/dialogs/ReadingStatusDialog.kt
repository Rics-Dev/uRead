package com.ricdev.uread.presentation.sharedComponents.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ricdev.uread.R
import com.ricdev.uread.data.model.ReadingStatus



@Composable
fun ReadingStatusDialog(
    currentStatus: ReadingStatus?,
    onStatusSelected: (ReadingStatus) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.change_reading_status)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ReadingStatusOption(
                    status = ReadingStatus.NOT_STARTED,
                    currentStatus = currentStatus,
                    onStatusSelected = onStatusSelected
                )
                ReadingStatusOption(
                    status = ReadingStatus.IN_PROGRESS,
                    currentStatus = currentStatus,
                    onStatusSelected = onStatusSelected
                )
                ReadingStatusOption(
                    status = ReadingStatus.FINISHED,
                    currentStatus = currentStatus,
                    onStatusSelected = onStatusSelected
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}



@Composable
fun ReadingStatusOption(
    status: ReadingStatus,
    currentStatus: ReadingStatus?,
    onStatusSelected: (ReadingStatus) -> Unit
) {
    val isSelected = status == currentStatus
    val buttonColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    Button(
        onClick = { onStatusSelected(status) },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (status) {
                    ReadingStatus.NOT_STARTED -> Icons.Outlined.Book
                    ReadingStatus.IN_PROGRESS -> Icons.Outlined.AutoStories
                    ReadingStatus.FINISHED -> Icons.Outlined.CheckCircle
                },
                contentDescription = status.name,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = when (status) {
                    ReadingStatus.NOT_STARTED -> stringResource(R.string.not_started)
                    ReadingStatus.IN_PROGRESS -> stringResource(R.string.in_progress)
                    ReadingStatus.FINISHED -> stringResource(R.string.finished)
                },
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}