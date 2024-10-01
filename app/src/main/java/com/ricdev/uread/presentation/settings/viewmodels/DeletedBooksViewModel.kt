package com.ricdev.uread.presentation.settings.viewmodels

import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.domain.use_case.books.DeleteBookUseCase
import com.ricdev.uread.domain.use_case.books.GetDeletedBooksUseCase
import com.ricdev.uread.domain.use_case.books.UpdateBookUseCase
import com.ricdev.uread.presentation.settings.states.DeletedBooksState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DeletedBooksViewModel @Inject constructor(
    private val getDeletedBooksUseCase: GetDeletedBooksUseCase,
    private val updateBookUseCase: UpdateBookUseCase,
    private val deleteBookUseCase: DeleteBookUseCase,
    application: Application,
) : AndroidViewModel(application) {

    private val _deletedBooksState = MutableStateFlow<DeletedBooksState>(DeletedBooksState.Loading)
    val deletedBooksState: StateFlow<DeletedBooksState> = _deletedBooksState.asStateFlow()

    private val appContext: Context = application.applicationContext


    init {
        getDeletedBooks()
    }


    private fun getDeletedBooks() {
        viewModelScope.launch {
            try {
                getDeletedBooksUseCase().collect { books ->
                    _deletedBooksState.value = DeletedBooksState.Success(books)
                }
            } catch (e: Exception) {
                _deletedBooksState.value =
                    DeletedBooksState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }


    fun restoreBooks(selectedBooks: Set<Book>) {
        viewModelScope.launch {
            selectedBooks.forEach { book ->
                val restoredBook = book.copy(deleted = false)
                updateBookUseCase(restoredBook)
            }
        }
    }


    fun permanentlyDeleteBooks(selectedBooks: Set<Book>) {
        viewModelScope.launch {
            selectedBooks.forEach { book ->
                val uri = Uri.parse(book.uri)
                if (uri.scheme == "content") {
                    // Use ContentResolver to delete the file
                    val contentResolver = appContext.contentResolver
                    val documentUri = DocumentsContract.buildDocumentUriUsingTree(
                        uri,
                        DocumentsContract.getDocumentId(uri)
                    )
                    if (DocumentsContract.deleteDocument(
                            contentResolver,
                            documentUri
                        )
                    ) {
                        deleteBookUseCase(book)
                    }
                } else {
                    // Handle cases where the URI is not a content URI (e.g., file://)
                    val bookFile = uri.path?.let { File(it) }
                    if (bookFile != null) {
                        if (bookFile.exists() && bookFile.delete()) {
                            deleteBookUseCase(book)
                        }
                    }
                }
            }
        }
    }
}