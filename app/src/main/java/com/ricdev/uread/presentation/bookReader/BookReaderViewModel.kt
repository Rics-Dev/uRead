package com.ricdev.uread.presentation.bookReader

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.data.model.BookAnnotation
import com.ricdev.uread.data.model.Bookmark
import com.ricdev.uread.data.model.Note
import com.ricdev.uread.data.model.ReaderPreferences
import com.ricdev.uread.data.model.ReadingActivity
import com.ricdev.uread.data.model.ReadingStatus
import com.ricdev.uread.data.model.toEpubPreferences
import com.ricdev.uread.data.source.local.AppPreferencesUtil
import com.ricdev.uread.data.source.local.ReaderPreferencesUtil
import com.ricdev.uread.domain.use_case.annotations.*
import com.ricdev.uread.domain.use_case.bookmarks.AddBookmarkUseCase
import com.ricdev.uread.domain.use_case.bookmarks.DeleteBookmarkUseCase
import com.ricdev.uread.domain.use_case.bookmarks.GetBookmarksForBookUseCase
import com.ricdev.uread.domain.use_case.bookmarks.UpdateBookmarkUseCase
import com.ricdev.uread.domain.use_case.books.GetBookByIdUseCase
import com.ricdev.uread.domain.use_case.books.UpdateBookUseCase
import com.ricdev.uread.domain.use_case.notes.AddNoteUseCase
import com.ricdev.uread.domain.use_case.notes.DeleteNoteUseCase
import com.ricdev.uread.domain.use_case.notes.GetNotesForBookUseCase
import com.ricdev.uread.domain.use_case.notes.UpdateNoteUseCase
import com.ricdev.uread.domain.use_case.reading_activity.AddReadingActivityUseCase
import com.ricdev.uread.domain.use_case.reading_activity.GetReadingActivityByDateUseCase
import com.ricdev.uread.domain.use_case.reading_progress.*
import com.ricdev.uread.util.PurchaseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.readium.navigator.media.tts.AndroidTtsNavigatorFactory
import org.readium.navigator.media.tts.TtsNavigator
import org.readium.navigator.media.tts.android.AndroidTtsEngine
import org.readium.navigator.media.tts.android.AndroidTtsPreferences
import org.readium.navigator.media.tts.android.AndroidTtsSettings
import org.readium.r2.navigator.DecorableNavigator
import org.readium.r2.navigator.Decoration
import org.readium.r2.navigator.VisualNavigator
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.util.*
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.streamer.PublicationOpener
import java.util.Calendar
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


@OptIn(ExperimentalReadiumApi::class)
@HiltViewModel
class BookReaderViewModel @Inject constructor(
    context: Application,
    private val appPreferencesUtil: AppPreferencesUtil,
    private val getBookByIdUseCase: GetBookByIdUseCase,
    private val updateBookUseCase: UpdateBookUseCase,
    private val getReadingProgressUseCase: GetReadingProgressUseCase,
    private val setReadingProgressUseCase: SetReadingProgressUseCase,
    private val getAnnotationsUseCase: GetAnnotationsUseCase,
    private val addAnnotationUseCase: AddAnnotationUseCase,
    private val updateAnnotationUseCase: UpdateAnnotationUseCase,
    private val deleteAnnotationUseCase: DeleteAnnotationUseCase,
    private val getNotesForBookUseCase: GetNotesForBookUseCase,
    private val addNotesUseCase: AddNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,

    private val getBookmarksForBookUseCase: GetBookmarksForBookUseCase,
    private val addBookmarksUseCase: AddBookmarkUseCase,
    private val updateBookmarkUseCase: UpdateBookmarkUseCase,
    private val deleteBookmarkUseCase: DeleteBookmarkUseCase,

    private val addOrUpdateReadingActivityUseCase: AddReadingActivityUseCase,
    private val getReadingActivityByDateUseCase: GetReadingActivityByDateUseCase,
    private val readerPreferencesUtil: ReaderPreferencesUtil,
    private val assetRetriever: AssetRetriever,
    private val publicationOpener: PublicationOpener,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(context) {

    private val _uiState = MutableStateFlow<BookReaderUiState>(BookReaderUiState.Loading)
    val uiState: StateFlow<BookReaderUiState> = _uiState.asStateFlow()

    private val _currentBookId = MutableStateFlow<Long?>(null)
    val currentBookId: StateFlow<Long?> = _currentBookId.asStateFlow()

    private val _initialLocator = MutableStateFlow<Locator?>(null)
    val initialLocator: StateFlow<Locator?> = _initialLocator.asStateFlow()

    private val _currentLocator = MutableStateFlow<Locator?>(null)
    val currentLocator: StateFlow<Locator?> = _currentLocator.asStateFlow()

    private val _appPreferences = MutableStateFlow(AppPreferencesUtil.defaultPreferences)
    val appPreferences: StateFlow<AppPreferences> = _appPreferences.asStateFlow()

    private val _readerPreferences = MutableStateFlow(ReaderPreferencesUtil.defaultPreferences)
    val readerPreferences: StateFlow<ReaderPreferences> = _readerPreferences.asStateFlow()

    private val _epubPreferences =
        MutableStateFlow(ReaderPreferencesUtil.defaultPreferences.toEpubPreferences())
    val epubPreferences: StateFlow<EpubPreferences> = _epubPreferences.asStateFlow()

    private val _annotations = MutableStateFlow<List<BookAnnotation>>(emptyList())
    val annotations: StateFlow<List<BookAnnotation>> = _annotations.asStateFlow()

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _bookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
    val bookmarks: StateFlow<List<Bookmark>> = _bookmarks.asStateFlow()

    private val _selectedAnnotation = MutableStateFlow<BookAnnotation?>(null)
    val selectedAnnotation: StateFlow<BookAnnotation?> = _selectedAnnotation.asStateFlow()

    private val _selectedNote = MutableStateFlow<Note?>(null)
    val selectedNote: StateFlow<Note?> = _selectedNote.asStateFlow()

    private val _book = MutableStateFlow<Book?>(null)
    val book: StateFlow<Book?> = _book.asStateFlow()


    private val _ttsNavigatorFactory = MutableStateFlow<AndroidTtsNavigatorFactory?>(null)
    private val ttsNavigatorFactory: StateFlow<AndroidTtsNavigatorFactory?> =
        _ttsNavigatorFactory.asStateFlow()

    private val _isTtsOn = MutableStateFlow(false)
    val isTtsOn: StateFlow<Boolean> = _isTtsOn.asStateFlow()

    private val _isTtsPlaying = MutableStateFlow(false)
    val isTtsPlaying: StateFlow<Boolean> = _isTtsPlaying.asStateFlow()

    private val _ttsNavigator =
        MutableStateFlow<TtsNavigator<AndroidTtsSettings, AndroidTtsPreferences, AndroidTtsEngine.Error, AndroidTtsEngine.Voice>?>(
            null
        )
    val ttsNavigator: StateFlow<TtsNavigator<AndroidTtsSettings, AndroidTtsPreferences, AndroidTtsEngine.Error, AndroidTtsEngine.Voice>?> =
        _ttsNavigator.asStateFlow()

    private val _ttsSpeed = MutableStateFlow<Double>(1.0)
    val ttsSpeed: StateFlow<Double> = _ttsSpeed.asStateFlow()
    private val _ttsPitch = MutableStateFlow(1.0)
    val ttsPitch: StateFlow<Double> = _ttsPitch.asStateFlow()


    private var isReadingSessionActive = false
    private var lastLocatorChangeTime = 0L
    private var currentDayStartTime = 0L


    init {

        val openedBookId = savedStateHandle.get<String>("bookId")?.toLongOrNull()
        val bookUri = savedStateHandle.get<String>("bookUri")
        resetCurrentDayStartTime()

        viewModelScope.launch {
            appPreferencesUtil.appPreferencesFlow.first().let { initialPreferences ->
                _appPreferences.value = initialPreferences
            }

            openedBookId?.let { bookId ->
                _currentBookId.value = bookId
                loadAnnotations(bookId)
                loadNotes(bookId)
                loadBookmarks(bookId)
                _initialLocator.value = getInitialLocator(bookId)
                bookUri?.let { uri ->
                    Uri.parse(uri).toAbsoluteUrl()?.let {
                        openBook(it, context)
                    }
                }

                // Fetch the book details
                fetchBook(bookId)


                readerPreferencesUtil.readerPreferencesFlow.collect { preferences ->
                    _readerPreferences.value = preferences
                    _epubPreferences.value = preferences.toEpubPreferences()
                }


                // Continue collecting preferences updates
                appPreferencesUtil.appPreferencesFlow.collect { preferences ->
                    _appPreferences.value = preferences
                }
            }
        }
    }


    fun initializeTtsNavigator(
        navigatorFragment: EpubNavigatorFragment?,
        context: Context,
    ) {
        viewModelScope.launch {
            val initialLocatorTts =
                (navigatorFragment as? VisualNavigator)?.firstVisibleElementLocator()

            ttsNavigatorFactory.value?.createNavigator(
                listener = object : TtsNavigator.Listener {
                    override fun onStopRequested() {
                        _isTtsOn.value = false
                        _isTtsPlaying.value = false
                    }
                },
                initialLocator = initialLocatorTts
            )?.onSuccess { navigator ->
                _ttsNavigator.value = navigator

                // Set TTS Preferences
                val ttsPreferences = AndroidTtsPreferences(
                    language = Language("en"),
                    pitch = 1.0,
                    speed = 1.0
                )
                navigator.submitPreferences(ttsPreferences)

                // Highlight Spoken Utterances
                val visualNavigator: DecorableNavigator = navigatorFragment as DecorableNavigator

                combine(
                    navigator.location.map { it.utteranceLocator }.distinctUntilChanged(),
                    _isTtsOn
                ) { locator, isTtsOn ->
                    Pair(locator, isTtsOn)
                }.onEach { (locator, isTtsOn) ->
                    if (isTtsOn) {
                        visualNavigator.applyDecorations(
                            listOf(
                                Decoration(
                                    id = "tts-utterance",
                                    locator = locator,
                                    style = Decoration.Style.Highlight(tint = Color.Red.toArgb())
                                )
                            ), group = "tts"
                        )
                    } else {
                        visualNavigator.applyDecorations(emptyList(), group = "tts")
                    }
                }.launchIn(viewModelScope)

                fun <T> Flow<T>.throttleLatest(period: Duration): Flow<T> = flow {
                    conflate().collect {
                        emit(it)
                        delay(period)
                    }
                }

                navigator.location
                    .throttleLatest(1.seconds)
                    .map { it.tokenLocator ?: it.utteranceLocator }
                    .distinctUntilChanged()
                    .onEach { locator ->
                        navigatorFragment.go(locator)
                    }
                    .launchIn(viewModelScope)

                // Handle playback errors
                navigator.playback
                    .onEach { playback ->
                        val failureState = playback.state as? TtsNavigator.State.Failure
                        val error = failureState?.error

                        if (error?.message == AndroidTtsEngine.Error.Output.message) {
                            AndroidTtsEngine.requestInstallVoice(context)
                        }
                    }
                    .launchIn(viewModelScope)
            }?.onFailure { error ->
                // Handle the error
                println("Failed to create TTS Navigator: ${error.message}")
            }
        }
    }


    fun setTtsSpeed(speed: Double) {
        _ttsSpeed.value = speed
        updateTtsPreferences()
    }

    fun setTtsPitch(pitch: Double) {
        _ttsPitch.value = pitch
        updateTtsPreferences()
    }

    private fun updateTtsPreferences() {
        ttsNavigator.value?.submitPreferences(
            AndroidTtsPreferences(
                speed = _ttsSpeed.value,
                pitch = _ttsPitch.value,
                language = Language("en") // Assuming English is the default language
            )
        )
    }

    fun skipToNextUtterance() {
        viewModelScope.launch {
            ttsNavigator.value?.skipToNextUtterance()
        }
    }

    fun skipToPreviousUtterance() {
        viewModelScope.launch {
            ttsNavigator.value?.skipToPreviousUtterance()
        }
    }


    fun setTtsPlaying(isPlaying: Boolean) {
        _isTtsPlaying.value = isPlaying
    }

    fun toggleTts(
        navigatorFragment: EpubNavigatorFragment?,
        context: Context,
    ) {
        viewModelScope.launch {
            val navigator = _ttsNavigator.value
            val isTtsOn = _isTtsOn.value

            if (isTtsOn) {
                navigator?.pause()
                _isTtsOn.value = false
                _isTtsPlaying.value = false
            } else {
                initializeTtsNavigator(navigatorFragment, context)
                _ttsNavigator.collectLatest { newNavigator ->
                    if (newNavigator != null) {
                        newNavigator.play()
                        _isTtsOn.value = true
                        _isTtsPlaying.value = true
                    }
                }
            }
        }
    }


    fun fetchInitialLocator() {
        viewModelScope.launch {
            currentBookId.value?.let { bookId ->
                _initialLocator.value = getInitialLocator(bookId)
            }
        }
    }

    private suspend fun fetchBook(bookId: Long) {
        try {
            _book.value = getBookByIdUseCase(bookId)
        } catch (e: Exception) {
            _uiState.value = BookReaderUiState.Error(e.message ?: "An error occurred")
        }
    }

    private fun resetCurrentDayStartTime() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        currentDayStartTime = calendar.timeInMillis
    }


    private fun openBook(bookUri: AbsoluteUrl, context: Application) {
        viewModelScope.launch {
            try {
                val asset = assetRetriever.retrieve(bookUri).getOrElse { throw ErrorException(it) }
                val publication = publicationOpener.open(asset, allowUserInteraction = true)
                    .getOrElse { throw ErrorException(it) }
                _ttsNavigatorFactory.value = AndroidTtsNavigatorFactory(context, publication)
                _uiState.value = BookReaderUiState.Success(publication)
            } catch (e: Exception) {
                _uiState.value = BookReaderUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }


    private fun loadAnnotations(bookId: Long) {
        viewModelScope.launch {
            getAnnotationsUseCase(bookId).collect { annotationsList ->
                _annotations.value = annotationsList
            }
        }
    }

    fun getLatestAnnotations(): List<BookAnnotation> {
        return annotations.value
    }

    private fun loadNotes(bookId: Long) {
        viewModelScope.launch {
            getNotesForBookUseCase(bookId).collect { updatedNotes ->
                _notes.value = updatedNotes
            }
        }
    }

    private fun loadBookmarks(bookId: Long) {
        viewModelScope.launch {
            getBookmarksForBookUseCase(bookId).collect { bookmarks ->
                _bookmarks.value = bookmarks
            }
        }
    }

    fun addAnnotation(annotation: BookAnnotation) {
        viewModelScope.launch {
            val annotationId = addAnnotationUseCase(annotation)
            val newAnnotation = annotation.copy(id = annotationId)
            _annotations.value += newAnnotation
            _selectedAnnotation.value = newAnnotation
            currentBookId.value?.let { loadAnnotations(it) }
        }
    }

    fun addNote(note: Note) {
        viewModelScope.launch {
            addNotesUseCase(note)
            currentBookId.value?.let { loadNotes(it) }
        }
    }

    fun addBookmark(locator: Locator) {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            val newBookmark = Bookmark(
                locator = locator.toJSON().toString(),
                bookId = _currentBookId.value!!,
                dateAndTime = currentTime
            )
            addBookmarksUseCase(newBookmark)
            currentBookId.value?.let { loadBookmarks(it) }
        }
    }


    fun updateAnnotation(annotation: BookAnnotation) {
        viewModelScope.launch {
            updateAnnotationUseCase(annotation)
            _annotations.value += annotation
            _selectedAnnotation.value = annotation
            currentBookId.value?.let { loadAnnotations(it) }

        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            updateNoteUseCase(note)
            // Update the notes list immediately
            _notes.update { currentNotes ->
                currentNotes.map { if (it.id == note.id) note else it }
            }
            // Update the selected note if it's the one being edited
            _selectedNote.update { selectedNote ->
                if (selectedNote?.id == note.id) note else selectedNote
            }
        }
    }

    fun deleteAnnotation(annotation: BookAnnotation) {
        viewModelScope.launch {
            deleteAnnotationUseCase(annotation)
            _annotations.update { currentAnnotations ->
                currentAnnotations.filter { it.id != annotation.id }
            }
            _selectedAnnotation.value = null
            currentBookId.value?.let { loadAnnotations(it) }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            deleteNoteUseCase(note)
            currentBookId.value?.let { loadNotes(it) }
        }
    }


    fun deleteBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            deleteBookmarkUseCase(bookmark)
            currentBookId.value?.let { loadBookmarks(it) }
        }
    }


    fun selectAnnotation(annotation: BookAnnotation) {
        _selectedAnnotation.value = annotation
    }

    fun selectNote(noteId: Long) {
        val note = _notes.value.find { it.id == noteId }
        _selectedNote.value = note
    }

    fun clearSelectedAnnotation() {
        _selectedAnnotation.value = null
    }

    fun clearSelectedNote() {
        _selectedNote.value = null
    }

    fun updateReaderPreferences(newPreferences: ReaderPreferences) {
        viewModelScope.launch {
            readerPreferencesUtil.updatePreferences(newPreferences)
        }
    }

    fun resetFontPreferences() {
        viewModelScope.launch {
            readerPreferencesUtil.resetFontPreferences()
        }
    }

    fun resetPagePreferences() {
        viewModelScope.launch {
            readerPreferencesUtil.resetPagePreferences()
        }
    }

    fun resetUiPreferences() {
        viewModelScope.launch {
            readerPreferencesUtil.resetUiPreferences()
        }
    }

    fun resetReaderPreferences() {
        viewModelScope.launch {
            readerPreferencesUtil.resetReaderPreferences()
        }
    }


    private suspend fun saveReadingProgress(locator: Locator) {
        currentBookId.value?.let { bookId ->
            setReadingProgressUseCase(bookId, locator.toJSON().toString())
        }
    }

    private suspend fun getInitialLocator(bookId: Long): Locator? {
        return getReadingProgressUseCase(bookId).let { progressJson ->
            if (progressJson.isNotEmpty()) {
                Locator.fromJSON(JSONObject(progressJson))
            } else {
                null
            }
        }
    }


    fun resetReadingSession() {
        isReadingSessionActive = false
        lastLocatorChangeTime = 0L
    }


    fun updateCurrentLocator(locator: Locator) {
        viewModelScope.launch {
            _currentLocator.value = locator
            saveReadingProgress(locator)
            if (isReadingSessionActive) {
                updateReadingTime()
            } else {
                isReadingSessionActive = true
                lastLocatorChangeTime = System.currentTimeMillis()
            }
            updateStartReadingDate()

            val progression = locator.locations.totalProgression ?: 0.0
            if (progression >= 0.99) {
                updateEndReadingDate()
            }
        }
    }


    private suspend fun updateReadingTime() {
        val currentTime = System.currentTimeMillis()
        if (lastLocatorChangeTime != 0L) {
            val sessionDuration = currentTime - lastLocatorChangeTime
            updateBookReadingTime(sessionDuration)
            updateReadingActivity(sessionDuration)
        }
        lastLocatorChangeTime = currentTime
    }


    private suspend fun updateBookReadingTime(sessionDuration: Long) {
        currentBookId.value?.let { bookId ->
            val book = getBookByIdUseCase(bookId)
            book?.let {
                val updatedBook = it.copy(readingTime = it.readingTime + sessionDuration)
                updateBookUseCase(updatedBook)
            }
        }
    }


    private suspend fun updateReadingActivity(sessionDuration: Long) {
        currentBookId.value?.let { bookId ->
            val book = getBookByIdUseCase(bookId)
            book?.let {
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
    }


    private suspend fun updateStartReadingDate() {
        currentBookId.value?.let { bookId ->
            val book = getBookByIdUseCase(bookId)
            book?.let {
                if (it.startReadingDate == null) {
                    val updatedBook = it.copy(
                        startReadingDate = System.currentTimeMillis(),
                    )
                    updateBookUseCase(updatedBook)
                }
            }
        }
    }

    private suspend fun updateEndReadingDate() {
        currentBookId.value?.let { bookId ->
            val book = getBookByIdUseCase(bookId)
            book?.let {
                if (it.endReadingDate == null) {
                    val updatedBook = it.copy(
                        endReadingDate = System.currentTimeMillis(),
                        readingStatus = ReadingStatus.FINISHED
                    )
                    updateBookUseCase(updatedBook)
                }
            }
        }
    }







    fun purchasePremium(purchaseHelper: PurchaseHelper) {
        purchaseHelper.makePurchase()
        viewModelScope.launch {
            purchaseHelper.isPremium.collect { isPremium ->
                updatePremiumStatus(isPremium)
            }
        }
    }

    fun updatePremiumStatus(isPremium: Boolean) {
        viewModelScope.launch {
            val currentPreferences = appPreferences.value
            if (currentPreferences.isPremium != isPremium) {
                val updatedPreferences = currentPreferences.copy(isPremium = isPremium)
                appPreferencesUtil.updateAppPreferences(updatedPreferences)
                _appPreferences.value = updatedPreferences
            }
        }
    }


}