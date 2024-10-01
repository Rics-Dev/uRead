package com.ricdev.uread.presentation.audioBookReader

sealed class LoadingState {
    data object Loading : LoadingState()
    data object BookLoaded : LoadingState()
    data object InitializingPlayer : LoadingState()
    data object Ready : LoadingState()
    data class Error(val message: String) : LoadingState()
}