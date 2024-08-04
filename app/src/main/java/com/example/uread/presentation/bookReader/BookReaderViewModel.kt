package com.example.uread.presentation.bookReader

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.uread.data.source.local.BookDao
import com.example.uread.domain.use_case.GetBookUrisUseCase
import com.example.uread.domain.use_case.GetBooksUseCase
import com.example.uread.domain.use_case.reading_progress.GetReadingProgressUseCase
import com.example.uread.domain.use_case.reading_progress.SetReadingProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import org.readium.r2.shared.DelicateReadiumApi
import org.readium.r2.shared.publication.Locator
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
    context: Application,
    private val getReadingProgressUseCase: GetReadingProgressUseCase,
    private val setReadingProgressUseCase: SetReadingProgressUseCase,
    private val assetRetriever: AssetRetriever,
    private val publicationOpener: PublicationOpener,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(context) {


    private val _uiState = MutableStateFlow<BookReaderUiState>(BookReaderUiState.Loading)
    val uiState: StateFlow<BookReaderUiState> = _uiState.asStateFlow()

    private val _currentBookUri = MutableStateFlow<String?>(null)
    val currentBookUri: StateFlow<String?> = _currentBookUri.asStateFlow()

    private val _initialLocator = MutableStateFlow<Locator?>(null)
    val initialLocator: StateFlow<Locator?> = _initialLocator.asStateFlow()


    init {
        savedStateHandle.get<String>("bookUri")?.let { bookUri ->
            _currentBookUri.value = bookUri
            viewModelScope.launch {
                _initialLocator.value = getInitialLocator(bookUri)
                Uri.parse(bookUri).toAbsoluteUrl()?.let { openBook(it) }
            }
//            Uri.parse(bookUri).toAbsoluteUrl()?.let { openBook(it) }
        }
    }


    fun saveReadingProgress(locator: Locator) {
        viewModelScope.launch {
            currentBookUri.value?.let { uri ->
                setReadingProgressUseCase(uri, locator.toJSON().toString())
            }
        }
    }

    private suspend fun getInitialLocator(bookUri: String): Locator? {
        return getReadingProgressUseCase(bookUri).let { progressJson ->
            if (progressJson.isNotEmpty()) {
                Locator.fromJSON(JSONObject(progressJson))
            } else {
                null
            }
        }
    }

    private fun openBook(bookUri: AbsoluteUrl) {
        viewModelScope.launch {
            try {
                val asset = assetRetriever.retrieve(bookUri).getOrElse { throw ErrorException(it) }
                val publication = publicationOpener.open(asset, allowUserInteraction = true)
                    .getOrElse { throw ErrorException(it) }

                _uiState.value = BookReaderUiState.Success(publication)
            } catch (e: Exception) {
                _uiState.value = BookReaderUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

}







