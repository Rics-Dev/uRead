package com.ricdev.uread.presentation.bookDetails

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import java.io.File
import java.security.MessageDigest
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

    private val _updateError = MutableStateFlow<String?>(null)
    val updateError: StateFlow<String?> = _updateError.asStateFlow()



    init {
        val bookId = savedStateHandle.get<String>("bookId")?.toLongOrNull()
        if (bookId != null) {
            viewModelScope.launch {
                _book.value = getBookByIdUseCase(bookId)
            }
        }
    }


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


    fun updateCoverImage(context: Context, bookId: String, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val imageBytes = inputStream?.readBytes()
            inputStream?.close()

            if (imageBytes == null) return null

            val md = MessageDigest.getInstance("MD5")
            val imageHash = md.digest(imageBytes).joinToString("") { "%02x".format(it) }

            // **Ensure filename is unique per book**
            val fileName = "cover_${bookId}_$imageHash.jpg"
            val file = File(context.filesDir, fileName)

            // **Delete the old cover of this specific book**
            context.filesDir.listFiles()?.forEach { existingFile ->
                if (existingFile.name.startsWith("cover_${bookId}_") && existingFile.name != fileName) {
                    existingFile.delete()
                }
            }

            // Save new cover image
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            file.outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                out.flush()
            }

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }




//    fun updateCoverImage(context: Context, uri: Uri): String? {
//        return try {
//            val inputStream = context.contentResolver.openInputStream(uri)
//            val imageBytes = inputStream?.readBytes()
//            inputStream?.close()
//
//            if (imageBytes == null) return null
//
//            val md = MessageDigest.getInstance("MD5")
//            val imageHash = md.digest(imageBytes).joinToString("") { "%02x".format(it) }
//            val fileName = "${imageHash}.jpg"
//
//            val file = File(context.filesDir, fileName)
//
//            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
//            file.outputStream().use { out ->
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
//                out.flush()
//            }
//
//            file.absolutePath
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
}