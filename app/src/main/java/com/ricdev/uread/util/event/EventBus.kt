package com.ricdev.uread.util.event

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

class EventBus @Inject constructor() {
    private val _events = MutableSharedFlow<AppEvent>()
    val events = _events.asSharedFlow()

    suspend fun emitEvent(event: AppEvent) {
        _events.emit(event)
    }
}