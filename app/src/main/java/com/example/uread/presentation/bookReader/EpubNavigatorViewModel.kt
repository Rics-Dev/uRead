package com.example.uread.presentation.bookReader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EpubNavigatorViewModel @Inject constructor(
) : ViewModel() {

    private val _fontSize = MutableStateFlow(16.0)
    val fontSize: StateFlow<Double> = _fontSize.asStateFlow()

    fun setFontSize(newFontSize: Double) {
        viewModelScope.launch {
            _fontSize.value = newFontSize
        }
    }
}
