package com.ricdev.uread.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ricdev.uread.R
import com.ricdev.uread.presentation.home.states.ImportProgressState
import com.ricdev.uread.presentation.home.states.SnackbarState


@Composable
fun AddBookSnackbar(
    snackbarState: SnackbarState,
    importProgressState: ImportProgressState,
    onDismiss: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarState, importProgressState) {
        when (snackbarState) {
            is SnackbarState.Visible -> {
                snackbarHostState.showSnackbar(
                    message = when (importProgressState) {
                        is ImportProgressState.InProgress -> "Adding books: ${importProgressState.current}/${importProgressState.total}"
                        is ImportProgressState.Error -> "Error: ${importProgressState.message}"
                        ImportProgressState.Complete -> snackbarState.message
                        ImportProgressState.Idle -> snackbarState.message
                    },
                    actionLabel = snackbarState.actionLabel,
                    duration = if (snackbarState.isIndefinite) SnackbarDuration.Indefinite else SnackbarDuration.Short,
                ).let { result ->
                    when (result) {
                        SnackbarResult.ActionPerformed -> snackbarState.onActionClick?.invoke()
                        SnackbarResult.Dismissed -> onDismiss()
                    }
                }
            }
            SnackbarState.Hidden -> snackbarHostState.currentSnackbarData?.dismiss()
        }
    }

    SnackbarHost(hostState = snackbarHostState) { data ->
        Snackbar(
            modifier = Modifier
                .padding(16.dp)
                .wrapContentWidth(),
            action = data.visuals.actionLabel?.let { actionLabel ->
                {
                    TextButton(onClick = { data.performAction() }) {
                        Text(actionLabel)
                    }
                }
            },
            dismissAction = if (snackbarState !is SnackbarState.Visible || !snackbarState.isIndefinite) {
                {
                    TextButton(onClick = { data.dismiss() }) {
                        Text(stringResource(R.string.dismiss))
                    }
                }
            } else null,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.wrapContentWidth()
            ) {
                if (snackbarState is SnackbarState.Visible && snackbarState.showProgress) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 3.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(16.dp))
                }
                Text(data.visuals.message)
            }
        }
    }
}
