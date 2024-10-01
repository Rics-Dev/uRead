package com.ricdev.uread.presentation.bookDetails

import android.app.Application
import android.content.ContentResolver
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.data.model.ReadingStatus
import com.ricdev.uread.domain.use_case.books.GetBookByIdUseCase
import com.ricdev.uread.domain.use_case.books.UpdateBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.readium.r2.shared.publication.Publication
import javax.inject.Inject

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    application: Application,
    private val getBookByIdUseCase: GetBookByIdUseCase,
    private val updateBookUseCase: UpdateBookUseCase,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val _book = MutableStateFlow<Book?>(null)
    val book: StateFlow<Book?> = _book.asStateFlow()

    private val _bookMetadata = MutableStateFlow(mapOf<String, String>())
    val bookMetadata: StateFlow<Map<String, String>> = _bookMetadata.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating.asStateFlow()

    private val _updateError = MutableStateFlow<String?>(null)
    val updateError: StateFlow<String?> = _updateError.asStateFlow()

    private var publication: Publication? = null

    private val contentResolver: ContentResolver = getApplication<Application>().contentResolver

    init {
        val bookId = savedStateHandle.get<String>("bookId")?.toLongOrNull()
        if (bookId != null) {
            viewModelScope.launch {
                _book.value = getBookByIdUseCase(bookId)
//                _book.value?.let { loadPublication(it.uri) }
            }
        }
    }

//    private fun loadPublication(uri: String) {
//        viewModelScope.launch {
//            try {
//                val fileContents = readFileContents(Uri.parse(uri))
//                val py = Python.getInstance()
//                val editMetadataModule = py.getModule("edit_metadata")
//                val pyBytes = py.getBuiltins().callAttr("bytes", fileContents)
//                val metadataJson = editMetadataModule.callAttr("get_metadata", pyBytes).toString()
//                val metadata = JSONObject(metadataJson)
//
//                Log.d("metadata", metadata.toString())
//
//                _bookMetadata.value = mapOf(
//                    "title" to metadata.optString("title", ""),
//                    "author" to metadata.optString("authors", ""),
//                    "description" to metadata.optString("description", "")
//                )
//            } catch (e: Exception) {
//                _updateError.value = "Failed to load publication: ${e.message}"
//                Log.d("error python", e.message.toString())
//            }
//        }
//    }

    fun updateBook(updatedBook: Book, updatedReadingStatus: Boolean = false) {
        viewModelScope.launch {
            var updateBook: Book = updatedBook
            if (updatedReadingStatus) {
                updateBook = when (updatedBook.readingStatus) {
                    ReadingStatus.NOT_STARTED -> updatedBook.copy(
                        startReadingDate = null,
                        endReadingDate = null,
                        readingTime = 0,
                        progression = 0f
                    )
                    ReadingStatus.IN_PROGRESS -> updatedBook.copy(
                        startReadingDate = System.currentTimeMillis(),
                        endReadingDate = null,
                        readingTime = 0,
                        progression = 0f
                    )
                    ReadingStatus.FINISHED -> updatedBook.copy(
                        endReadingDate = System.currentTimeMillis(),
                        progression = 100f,
                    )
                    else -> updatedBook
                }
            }

            updateBookUseCase(updateBook)
            _book.value = getBookByIdUseCase(updatedBook.id)
        }
    }

//    fun updateMetadata(
//        title: String?,
//        authors: String?,
//        description: String?
//    ) {
//        viewModelScope.launch(Dispatchers.IO) {
//            _isUpdating.value = true
//            _updateError.value = null
//
//            try {
//                val uri = Uri.parse(_book.value?.uri)
//                val fileContents = readFileContents(uri)
//
//                val py = Python.getInstance()
//                val editMetadataModule = py.getModule("edit_metadata")
//                val pyBytes = py.getBuiltins().callAttr("bytes", fileContents)
//                val updatedContents = editMetadataModule.callAttr(
//                    "edit_metadata",
//                    pyBytes,
//                    title,
//                    authors,
//                    description
//                ).toJava(ByteArray::class.java)
//
//                writeFileContents(uri, updatedContents)
//
//                _bookMetadata.value = mapOf(
//                    "title" to (title ?: ""),
//                    "author" to (authors ?: ""),
//                    "description" to (description ?: "")
//                )
//                // Update the book object if necessary
//                _book.value = _book.value?.copy(
//                    title = title ?: _book.value?.title ?: "",
//                    authors = authors ?: _book.value?.authors ?: ""
//                )
//            } catch (e: Exception) {
//                _updateError.value = "Error updating metadata: ${e.message}"
//                Log.d("error python", e.message.toString())
//            } finally {
//                _isUpdating.value = false
//            }
//        }
//    }
//
//    private suspend fun readFileContents(uri: Uri): ByteArray = withContext(Dispatchers.IO) {
//        contentResolver.openInputStream(uri)?.use { it.readBytes() }
//            ?: throw IOException("Failed to read file contents")
//    }
//
//    private suspend fun writeFileContents(uri: Uri, contents: ByteArray) = withContext(Dispatchers.IO) {
//        contentResolver.openOutputStream(uri)?.use { it.write(contents) }
//            ?: throw IOException("Failed to write file contents")
//    }
}