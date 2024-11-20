package com.ricdev.uread.presentation.home.states



// Define sealed class for import states
sealed class ImportProgressState {
    data object Idle : ImportProgressState()
    data class InProgress(val current: Int, val total: Int) : ImportProgressState()
    data class Error(val message: String) : ImportProgressState()
    data object Complete : ImportProgressState()
}
