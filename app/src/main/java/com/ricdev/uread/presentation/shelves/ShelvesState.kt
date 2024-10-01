package com.ricdev.uread.presentation.shelves

import com.ricdev.uread.data.model.Shelf

sealed class ShelvesState {
    data object Loading : ShelvesState()
    data class Error(val message: String) : ShelvesState()
    data class Success(val shelves: List<Shelf>) : ShelvesState()
}