package com.ricdev.uread.presentation.home.states




sealed class SnackbarState {
    data object Hidden : SnackbarState()
    data class Visible(
        val message: String,
        val isIndefinite: Boolean = false,
        val showProgress: Boolean = false,
        val actionLabel: String? = null,
        val onActionClick: (() -> Unit)? = null
    ) : SnackbarState()
}
