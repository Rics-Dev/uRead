package com.ricdev.uread.presentation.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ricdev.uread.presentation.home.states.ImportProgressState
import com.ricdev.uread.presentation.home.states.SnackbarState



@Composable
fun CustomSnackbar(
    snackbarState: SnackbarState,
    importProgressState: ImportProgressState,
) {
    when (snackbarState) {
        is SnackbarState.Visible -> {
            Snackbar(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
//                action = {
//                    // Optional dismiss button
//                    TextButton(onClick = onDismiss) {
//                        Text("Dismiss")
//                    }
//                }
//                dismissAction = {
//                    TextButton(onClick = onDismiss) {
//                        Text("Dismiss")
//                    }
//                },
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                // Show different content based on import progress
                when (importProgressState) {
                    is ImportProgressState.InProgress -> {
                        val animatedProgress = animateFloatAsState(
                            targetValue = importProgressState.current.toFloat() / importProgressState.total,
                            label = ""
                        ).value
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                text = snackbarState.message
                            )
                        }
                    }
                    is ImportProgressState.Error -> {
                        Text(
                            text = snackbarState.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    is ImportProgressState.Complete -> {
                        Text(text = snackbarState.message)
                    }
                    ImportProgressState.Idle -> {
                        Text(text = snackbarState.message)
                    }
                }
            }
        }
        SnackbarState.Hidden -> {
            // Do nothing when hidden
        }
    }
}