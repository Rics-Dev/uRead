package com.ricdev.uread.util.event

sealed class AppEvent {
    data object RefreshBooks : AppEvent()
}