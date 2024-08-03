package com.example.uread.presentation.bookReader

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.readium.r2.shared.DelicateReadiumApi
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.ErrorException
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.getOrElse
import org.readium.r2.shared.util.toAbsoluteUrl
import org.readium.r2.streamer.PublicationOpener
import javax.inject.Inject

@OptIn(DelicateReadiumApi::class)
@HiltViewModel
class BookReaderViewModel @Inject constructor(
    private val context: Application,
    private val assetRetriever: AssetRetriever,
    private val publicationOpener: PublicationOpener,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(context) {

    private val _publication = MutableStateFlow<Publication?>(null)
    val publication: StateFlow<Publication?> = _publication.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()


    init {
        savedStateHandle.get<String>("bookUri")?.let { bookUri ->
            val bookUrl = Uri.parse(bookUri).toAbsoluteUrl()
            if (bookUrl != null) {
                openBook(bookUrl)
            }
        }
    }

    private fun openBook(bookUri: AbsoluteUrl) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val asset = bookUri.let {
                    assetRetriever.retrieve(it)
                        .getOrElse { throw ErrorException(it) }
                }

                val pub = asset.let {
                    publicationOpener.open(it, allowUserInteraction = true)
                        .getOrElse { throw ErrorException(it) }
                }

                _publication.value = pub
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }
}