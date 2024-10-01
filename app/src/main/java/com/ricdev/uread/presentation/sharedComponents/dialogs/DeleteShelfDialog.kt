package com.ricdev.uread.presentation.sharedComponents.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ricdev.uread.R
import com.ricdev.uread.data.model.Shelf

@Composable
fun DeleteShelfDialog(
    selectedShelf: Shelf?,
    onDismiss: () -> Unit,
    onConfirmDelete: (Shelf) -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(onClick = {
                if (selectedShelf != null) {
                    onConfirmDelete(selectedShelf)
                }
                onDismiss()
            }) {
                Text(text = stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
            }) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        title = {
            Text(text = stringResource(R.string.delete_shelf))
        },
        text = {
            if (selectedShelf != null) {
                Text(
                    text = stringResource(
                        R.string.are_you_sure_you_want_to_delete,
                        selectedShelf.name
                    )
                )
            }
        }
    )
}