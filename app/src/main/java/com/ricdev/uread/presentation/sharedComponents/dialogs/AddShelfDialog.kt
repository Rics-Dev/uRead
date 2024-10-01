package com.ricdev.uread.presentation.sharedComponents.dialogs

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ricdev.uread.R

@Composable
fun AddShelfDialog(
    newShelfName: String,
    onShelfNameChange: (String) -> Unit,
    shelves: List<String>,
    onAddShelf: (String) -> Unit,
    onDismiss: () -> Unit,
    context: Context,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(stringResource(R.string.add_new_shelf)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = newShelfName,
                    onValueChange = onShelfNameChange,
                    label = { Text(stringResource(R.string.shelf_name)) }
                )

                Text(text = stringResource(R.string.required), style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when {
                        newShelfName.isBlank() -> {
                            Toast.makeText(context, "Shelf name is required", Toast.LENGTH_SHORT)
                                .show()
                        }

                        shelves.any { it.equals(newShelfName, ignoreCase = true) } -> {
                            Toast.makeText(context, "Shelf name already exists", Toast.LENGTH_SHORT)
                                .show()
                        }

                        else -> {
                            onAddShelf(newShelfName)
                            onShelfNameChange("")
                            onDismiss()
                        }
                    }
                }
            ) {
                Text(stringResource(R.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}