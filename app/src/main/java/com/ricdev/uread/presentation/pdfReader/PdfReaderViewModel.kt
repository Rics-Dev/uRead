package com.ricdev.uread.presentation.pdfReader

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.data.model.ReadingActivity
import com.ricdev.uread.data.model.ReadingStatus
import com.ricdev.uread.domain.use_case.books.GetBookByIdUseCase
import com.ricdev.uread.domain.use_case.books.UpdateBookUseCase
import com.ricdev.uread.domain.use_case.reading_activity.AddReadingActivityUseCase
import com.ricdev.uread.domain.use_case.reading_activity.GetReadingActivityByDateUseCase
import com.ricdev.uread.domain.use_case.reading_progress.GetReadingProgressUseCase
import com.ricdev.uread.util.PdfBitmapConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class PdfReaderViewModel @Inject constructor(
    private val getBookByIdUseCase: GetBookByIdUseCase,
    private val pdfBitmapConverter: PdfBitmapConverter,
    private val updateBookUseCase: UpdateBookUseCase,
    private val getReadingProgressUseCase: GetReadingProgressUseCase,
    private val addOrUpdateReadingActivityUseCase: AddReadingActivityUseCase,
    private val getReadingActivityByDateUseCase: GetReadingActivityByDateUseCase,
    savedStateHandle: SavedStateHandle,
    context: Application,
) : AndroidViewModel(context) {

    private val _book = MutableStateFlow<Book?>(null)
    val book = _book.asStateFlow()

    private val _pdfPages = MutableStateFlow<List<Bitmap?>>(emptyList())
    val pdfPages = _pdfPages.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _backgroundColor = MutableStateFlow(Color.White)
    val backgroundColor = _backgroundColor.asStateFlow()

    private val _pageCount = MutableStateFlow(0)
    val pageCount = _pageCount.asStateFlow()

    private val _initialPage = MutableStateFlow(0)
    val initialPage = _initialPage.asStateFlow()

    private val _pdfId = MutableStateFlow<Long>(-1)
//    val pdfId = _pdfId.asStateFlow()


    private lateinit var contentUri: Uri
    private val pageCache = mutableMapOf<Int, Bitmap>()
    private var readingStartTime: Long = 0
    private var lastSaveTime: Long = 0


    init {
        val pdfId = savedStateHandle.get<String>("bookId")?.toLongOrNull()
        val pdfUri = savedStateHandle.get<String>("bookUri")

        viewModelScope.launch {
            _isLoading.value = true
            if (pdfId != null && pdfUri != null) {
                _pdfId.value = pdfId
                contentUri = Uri.parse(pdfUri)
                initializePdfInfo()
                _book.value = getBookByIdUseCase(pdfId)
                startReadingSession()
            } else {
                _errorMessage.value = "Invalid PDF ID or URI"
                _isLoading.value = false
            }
        }
    }


    private suspend fun initializePdfInfo() {
        try {
            _pageCount.value = pdfBitmapConverter.getPageCount(contentUri)
            _pdfPages.value = List(_pageCount.value) { null }

            val savedProgress = getReadingProgressUseCase(_pdfId.value)
            val savedPage = savedProgress.toIntOrNull() ?: 0
            _initialPage.value = savedPage

            _isLoading.value = false
        } catch (e: Exception) {
            _errorMessage.value = "Failed to load PDF: ${e.message}"
            _isLoading.value = false
        }
    }

    fun loadInitialPages() {
        viewModelScope.launch {
            (0 until minOf(3, _pageCount.value)).forEach { loadPage(it) }
        }
    }

    fun loadPage(index: Int) {
        if (index < 0 || index >= _pageCount.value) return

        viewModelScope.launch {
            try {
                val bitmap = pageCache[index] ?: pdfBitmapConverter.pdfToBitmap(contentUri, index)
                bitmap.let {
                    pageCache[index] = it
                    val currentPages = _pdfPages.value.toMutableList()
                    currentPages[index] = it
                    _pdfPages.value = currentPages
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load page ${index + 1}: ${e.message}"
            }
        }
    }


    fun saveReadingProgress(currentPage: Int) {
        viewModelScope.launch {
            _book.value?.let { book ->
                val currentTime = System.currentTimeMillis()
                val sessionDuration = currentTime - lastSaveTime
                lastSaveTime = currentTime

                val newProgression = ((currentPage ).toFloat() / _pageCount.value.toFloat()) * 100f
                val newReadingTime = book.readingTime + sessionDuration
                var newReadingStatus = book.readingStatus

                if (newProgression >= 98f) {
                    newReadingStatus = ReadingStatus.FINISHED
                } else if (newReadingStatus != ReadingStatus.IN_PROGRESS) {
                    newReadingStatus = ReadingStatus.IN_PROGRESS
                }

                val updatedBook = book.copy(
                    locator = currentPage.toString(),
                    progression = newProgression,
                    readingTime = newReadingTime,
                    readingStatus = newReadingStatus,
                    endReadingDate = if (newReadingStatus == ReadingStatus.FINISHED) currentTime else null
                )

                updateBookUseCase(updatedBook)
                _book.value = updatedBook

                updateReadingActivity(sessionDuration)
            }
        }
    }

    private suspend fun updateReadingActivity(sessionDuration: Long) {
        _book.value?.let {
            val currentDate = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val existingActivity = getReadingActivityByDateUseCase(currentDate)
            if (existingActivity != null) {
                val updatedActivity = existingActivity.copy(
                    readingTime = existingActivity.readingTime + sessionDuration
                )
                addOrUpdateReadingActivityUseCase(updatedActivity)
            } else {
                val newActivity = ReadingActivity(
                    date = currentDate,
                    readingTime = sessionDuration
                )
                addOrUpdateReadingActivityUseCase(newActivity)
            }
        }
    }

    private fun startReadingSession() {
        readingStartTime = System.currentTimeMillis()
        lastSaveTime = readingStartTime
        _book.value?.let { book ->
            if (book.startReadingDate == null) {
                updateBook(book.copy(startReadingDate = readingStartTime))
            }
        }
    }


    private fun updateBook(updatedBook: Book) {
        viewModelScope.launch {
            var updatedBook2 = updatedBook
            if(updatedBook.progression >= 98f){
                updatedBook2 = updatedBook.copy(readingStatus = ReadingStatus.FINISHED)
            }
            updateBookUseCase(updatedBook2)
            _book.value = updatedBook2
        }
    }





}