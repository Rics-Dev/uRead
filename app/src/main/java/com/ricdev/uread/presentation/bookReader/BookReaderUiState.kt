package com.ricdev.uread.presentation.bookReader

import org.readium.r2.shared.publication.Publication

sealed class BookReaderUiState {
    data object Loading : BookReaderUiState()
    data class Error(val message: String) : BookReaderUiState()
    data class Success(val publication: Publication) : BookReaderUiState()
}