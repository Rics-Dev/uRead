package com.ricdev.uread.presentation.home.states




sealed class SnackbarState {
    data object Hidden : SnackbarState()
    data class Visible(
        val message: String,
        val unlimited: Boolean = false,
    ) : SnackbarState()
}
