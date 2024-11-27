package com.ricdev.uread.presentation.home


import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.data.model.FileType
import com.ricdev.uread.data.model.ReadingStatus
import com.ricdev.uread.data.model.Shelf
import com.ricdev.uread.data.model.SortOption
import com.ricdev.uread.data.model.SortOrder
import com.ricdev.uread.data.source.local.AppPreferencesUtil
import com.ricdev.uread.domain.use_case.books.DeleteBookByUriUseCase
import com.ricdev.uread.domain.use_case.books.DeleteBookUseCase
import com.ricdev.uread.domain.use_case.books.GetBookUrisUseCase
import com.ricdev.uread.domain.use_case.books.GetBooksUseCase
import com.ricdev.uread.domain.use_case.books.InsertBookUseCase
import com.ricdev.uread.domain.use_case.books.UpdateBookUseCase
import com.ricdev.uread.domain.use_case.shelves.AddBookToShelfUseCase
import com.ricdev.uread.domain.use_case.shelves.AddShelfUseCase
import com.ricdev.uread.domain.use_case.shelves.GetBooksForShelfUseCase
import com.ricdev.uread.domain.use_case.shelves.GetShelvesUseCase
import com.ricdev.uread.domain.use_case.shelves.RemoveBooksFromShelfUseCase
import com.ricdev.uread.domain.use_case.shelves.RemoveShelfUseCase
import com.ricdev.uread.presentation.home.states.ImportProgressState
import com.ricdev.uread.presentation.home.states.SnackbarState
import com.ricdev.uread.util.PurchaseHelper
import com.ricdev.uread.util.event.AppEvent
import com.ricdev.uread.util.event.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.services.cover
import org.readium.r2.shared.util.ErrorException
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.getOrElse
import org.readium.r2.shared.util.toAbsoluteUrl
import org.readium.r2.streamer.PublicationOpener
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject constructor(
    private val getBooksUseCase: GetBooksUseCase,
    private val getBookUrisUseCase: GetBookUrisUseCase,
    private val insertBookUseCase: InsertBookUseCase,
    private val updateBookUseCase: UpdateBookUseCase,
    private val deleteBookUseCase: DeleteBookUseCase,
    private val deleteBookByUriUseCase: DeleteBookByUriUseCase,
    private val addShelfUseCase: AddShelfUseCase,
    private val removeShelfUseCase: RemoveShelfUseCase,
    private val getShelvesUseCase: GetShelvesUseCase,
    private val addBookToShelfUseCase: AddBookToShelfUseCase,
    private val removeBooksFromShelfUseCase: RemoveBooksFromShelfUseCase,
    private val getBooksForShelfUseCase: GetBooksForShelfUseCase,
    private val assetRetriever: AssetRetriever,
    private val publicationOpener: PublicationOpener,
    private val appPreferencesUtil: AppPreferencesUtil,
    private val eventBus: EventBus,
    application: Application,
) : AndroidViewModel(application) {

    private val context: Context
        get() = getApplication<Application>().applicationContext

    private val _shelves = MutableStateFlow<List<Shelf>>(emptyList())
    val shelves: StateFlow<List<Shelf>> = _shelves.asStateFlow()


    private val _appPreferences = MutableStateFlow(AppPreferencesUtil.defaultPreferences)
    val appPreferences: StateFlow<AppPreferences> = _appPreferences.asStateFlow()


    private val _isAddingBooks = MutableStateFlow(false)
    val isAddingBooks: StateFlow<Boolean> = _isAddingBooks.asStateFlow()

    private val _books = MutableStateFlow<PagingData<Book>>(PagingData.empty())
    val books: StateFlow<PagingData<Book>> = _books.asStateFlow()

    private val _selectedBooks = MutableStateFlow<List<Book>>(emptyList())
    val selectedBooks: StateFlow<List<Book>> = _selectedBooks.asStateFlow()

    private val _selectionMode = MutableStateFlow(false)
    val selectionMode: StateFlow<Boolean> = _selectionMode.asStateFlow()


    private val _booksInShelfSet = MutableStateFlow<Set<Long>>(emptySet())
    val booksInShelfSet: StateFlow<Set<Long>> = _booksInShelfSet.asStateFlow()

    private val _currentShelf = MutableStateFlow<Shelf?>(null)
    private val currentShelf: StateFlow<Shelf?> = _currentShelf.asStateFlow()


    private val _selectedTabRow = MutableStateFlow(0)
    val selectedTabRow: StateFlow<Int> = _selectedTabRow.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()


    private var refreshJob: Job? = null

    private val _importProgressState = MutableStateFlow<ImportProgressState>(ImportProgressState.Idle)
    val importProgressState: StateFlow<ImportProgressState> = _importProgressState.asStateFlow()

    private val _snackbarState = MutableStateFlow<SnackbarState>(SnackbarState.Hidden)
    val snackbarState: StateFlow<SnackbarState> = _snackbarState.asStateFlow()

    private var snackbarJob: Job? = null









    init {
        initializeApp()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            val preferences = appPreferencesUtil.appPreferencesFlow.first()
            coroutineScope {
                launch { loadBooks(preferences) }
                launch { loadShelves() }
                launch { observeBooks(preferences) }
                launch { observeAppPreferences() }
                launch { observeEvents() }
            }
        }
    }

    private fun observeEvents() {
        viewModelScope.launch {
            eventBus.events.collect { event ->
                when (event) {
                    is AppEvent.RefreshBooks -> refreshBooks()
                }
            }
        }
    }

    private fun loadBooks(preferences: AppPreferences) {
        val sortBy = preferences.sortBy
        val sortOrder = preferences.sortOrder
        val readingStatus = preferences.readingStatus
        val fileType = preferences.fileTypes
        val isAscending = sortOrder == SortOrder.ASCENDING

        viewModelScope.launch {
            combine(
                getBooksUseCase(sortBy, isAscending, readingStatus, fileType).cachedIn(
                    viewModelScope
                ),
                searchQuery,
                currentShelf,
                booksInShelfSet,
                selectedTabRow
            ) { books, query, shelf, shelfBookIds, selectedTabRow ->
                books.filter { book ->
                    val matchesSearch =
                        query.isBlank() || book.title.contains(query, ignoreCase = true)
                    val matchesShelf = shelf == null || book.id in shelfBookIds
                    val matchesTab = if (selectedTabRow == 1) {
                        book.fileType == FileType.AUDIOBOOK
                    } else {
                        book.fileType != FileType.AUDIOBOOK
                    }
                    matchesSearch && matchesShelf && matchesTab
                }
            }.collect { filteredPagingData ->
                _books.value = filteredPagingData
            }

        }
    }


    private fun loadShelves() {
        viewModelScope.launch {
            getShelvesUseCase().collect { shelves ->
                _shelves.value = shelves
            }
        }
    }

    fun updateCurrentShelf(shelf: Shelf?) {
        _currentShelf.value = shelf
    }


    fun updateCurrentTabRow(tab: Int) {
        _selectedTabRow.value = tab
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun observeAppPreferences() {
        viewModelScope.launch {
            appPreferencesUtil.appPreferencesFlow.collect { preferences ->
                _appPreferences.value = preferences
                // Optionally reload books if sort preferences change
                loadBooks(preferences)
            }
        }
    }

    fun refreshBooks() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            delay(500)
            showSnackbar("Refreshing Library" )
            val scanDirectory = appPreferences.value.scanDirectories
            if (scanDirectory.isNotEmpty()) {
                observeBooks(appPreferences.value)
            } else {
                showSnackbar("No directory set for scanning books" )
            }
        }
    }



    private fun showSnackbar(
        message: String,
        unlimited: Boolean = false,
    ) {
        snackbarJob?.cancel()
        snackbarJob = viewModelScope.launch {
            _snackbarState.value = SnackbarState.Visible(
                message = message,
                unlimited = unlimited,
            )

            if(!unlimited) {
                delay(3000)
                hideSnackbar()
            }
        }


    }

    private fun hideSnackbar() {
        snackbarJob?.cancel()
        _snackbarState.value = SnackbarState.Hidden
    }

    private fun observeBooks(preferences: AppPreferences) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            _importProgressState.value =
                ImportProgressState.Error(throwable.message ?: "Unknown error occurred")
            _isAddingBooks.value = false
            showSnackbar(
                message = "Error during import: ${throwable.message}",
            )
        }) {
            try {

                val existingUris = getBookUrisUseCase()


                val documentFiles = mutableListOf<DocumentFile>()
                preferences.scanDirectories.forEach { directoryPath ->
                    val uri = Uri.parse(directoryPath)
                    val filesInDirectory = getBooksFromDirectory(context, uri)
                    documentFiles.addAll(filesInDirectory)
                }

                val uniqueFiles = documentFiles.distinctBy { it.uri.toString() }


                val newBooks = uniqueFiles.filter { documentFile ->
                    val bookUriString = documentFile.uri.toString()
                    !existingUris.contains(bookUriString)
                }

                val currentUris = uniqueFiles.map { it.uri.toString() }.toSet()
                val deletedUris = existingUris.filter { it !in currentUris }


                if (newBooks.isNotEmpty()) {
                    _isAddingBooks.value = true
                    _importProgressState.value = ImportProgressState.InProgress(0, newBooks.size)
                    showSnackbar(
                        message = "Adding new books to library",
                    )

                    // Process books in smaller batches
                    newBooks.chunked(5).forEachIndexed { batchIndex, batch ->
                        // Add delay between batches to prevent overwhelming the system
                        if (batchIndex > 0) delay(100)

                        batch.forEachIndexed { index, documentFile ->
                            try {
                                val totalProcessed = (batchIndex * 5) + index + 1
                                _importProgressState.value =
                                    ImportProgressState.InProgress(totalProcessed, newBooks.size)


                                // Update snackbar with progress
                                showSnackbar(
                                    message = "Adding books ($totalProcessed/${newBooks.size})",
                                    unlimited = true
                                )


                                // Check if book already exists before adding
                                val bookUriString = documentFile.uri.toString()
                                if (!getBookUrisUseCase().contains(bookUriString)) {
                                    addNewBook(documentFile)
                                }
                            } catch (e: Exception) {
                                Log.e("HomeViewModel", "Error adding book: ${documentFile.name}", e)
                            }
                        }
                    }

                    _importProgressState.value = ImportProgressState.Complete
                    showSnackbar(
                        message = "Added ${newBooks.size} book(s)",
                    )
                    _isAddingBooks.value = false
                }

                // Handle deleted books in batches
                if (deletedUris.isNotEmpty()) {
                    showSnackbar(
                        message = "Removing ${deletedUris.size} books",
                    )
                    deletedUris.chunked(10).forEach { batch ->
                        batch.forEach { bookUri ->
                            try {
                                deleteBookByUriUseCase(bookUri)
                            } catch (e: Exception) {
                                Log.e("HomeViewModel", "Error deleting book: $bookUri", e)
                            }
                        }
                        delay(50)
                    }
                    showSnackbar(
                        message = "Removed ${deletedUris.size} book(s)",
                    )
                }



                loadBooks(appPreferences.value)
            } catch (e: Exception) {
                _importProgressState.value = ImportProgressState.Error(e.message ?: "Unknown error occurred")
                Log.e("HomeViewModel", "Error observing books", e)
                showSnackbar(
                    message = "Error updating library: ${e.message}",
                )
            } finally {
                _isAddingBooks.value = false
            }
        }
    }

    fun updateAppPreferences(newPreferences: AppPreferences) {
        viewModelScope.launch {
            appPreferencesUtil.updateAppPreferences(newPreferences)
        }
    }

    fun resetLayoutPreferences() {
        viewModelScope.launch {
            appPreferencesUtil.resetLayoutPreferences()
        }
    }

    fun addShelf(shelfName: String) {
        viewModelScope.launch {
            try {
                val currentShelves = _shelves.value
                val newOrder = currentShelves.size
                val newShelfId = addShelfUseCase(shelfName, newOrder)
                _shelves.value += Shelf(id = newShelfId, name = shelfName, order = newOrder)
            } catch (e: Exception) {
                showSnackbar("Failed to add shelf: ${e.message}" )
            }
        }
    }

    fun removeShelf(shelf: Shelf) {
        viewModelScope.launch {
            try {
                removeShelfUseCase(shelf)
                _shelves.value = _shelves.value.filter { it.id != shelf.id }
            } catch (e: Exception) {
                showSnackbar("Failed to remove shelf: ${e.message}" )
            }
        }
    }

    fun removeBooks(books: List<Book>, hardRemove: Boolean) {
        viewModelScope.launch {
            try {
                if (hardRemove) {
                    books.forEach { book ->
                        try {
                            val uri = Uri.parse(book.uri)
                            if (uri.scheme == "content") {
                                // Use ContentResolver to delete the file
                                val contentResolver = context.contentResolver
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
                                } else {
                                    showSnackbar("Failed to delete book: ${book.title}" )
                                }
                            } else {
                                // Handle cases where the URI is not a content URI (e.g., file://)
                                val bookFile = uri.path?.let { File(it) }
                                if (bookFile != null) {
                                    if (bookFile.exists() && bookFile.delete()) {
                                        deleteBookUseCase(book)
                                    } else {
                                        showSnackbar("Failed to delete book: ${book.title}" )
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            showSnackbar(
                                "Failed to delete book: ${book.title} - ${e.message}"
                            )
                        }
                    }
                } else {
                    books.map { book ->
                        book.copy(deleted = true)
                    }.forEach {
                        updateBookUseCase(it)
                    }
                }
                showSnackbar("Books removed successfully" )
            } catch (e: Exception) {
                showSnackbar("Failed to delete books: ${e.message}" )
            }
        }
    }

    fun addBooksToShelves(bookIds: List<Long>, shelfIds: List<Long>) {
        viewModelScope.launch {
            try {
                bookIds.forEach { bookId ->
                    shelfIds.forEach { shelfId ->
                        addBookToShelfUseCase(bookId, shelfId)
                    }
                }
                showSnackbar("Books added to shelf successfully" )
            } catch (e: Exception) {
                showSnackbar("Failed to add books to shelf: ${e.message}" )
            }
        }
    }

    fun removeBooksFromShelves(bookIds: List<Long>, shelfIds: List<Long>) {
        viewModelScope.launch {
            try {
                bookIds.forEach { bookId ->
                    shelfIds.forEach { shelfId ->
                        removeBooksFromShelfUseCase(bookId, shelfId)
                    }
                }

                _booksInShelfSet.value -= bookIds.toSet()
                showSnackbar("Books removed from shelf successfully" )
            } catch (e: Exception) {
                showSnackbar("Failed to remove books from shelf: ${e.message}" )
            }
        }
    }





    fun getBooksForShelfSelection(shelfId: Long): Flow<List<Book>> {
        return getBooksForShelfUseCase(shelfId)
    }

    fun getBooksForShelf(shelfId: Long): List<Book> {
        var booksList: List<Book> = emptyList()
        viewModelScope.launch {
            getBooksForShelfUseCase(shelfId).collect { books: List<Book> ->
                booksList = books
                _booksInShelfSet.value = books.map { it.id }.toSet()
            }
        }
        return booksList
    }


    private suspend fun getBooksFromDirectory(context: Context, uri: Uri): List<DocumentFile> {
        return withContext(Dispatchers.IO) {
            try {
                val documentFile = DocumentFile.fromTreeUri(context, uri)
                documentFile?.let { scanDirectory(it) } ?: emptyList()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error scanning directory: $uri", e)
                emptyList()
            }
        }
    }


    private fun scanDirectory(directory: DocumentFile): List<DocumentFile> {
        return try {
            val allowedExtensions = listOf("epub", "pdf", "mp3", "m4a", "m4b", "aac").let {
                if (_appPreferences.value.enablePdfSupport) it else it - "pdf"
            }

            directory.listFiles().filter { file ->
                when {
                    file.isDirectory -> file.name?.let { !it.startsWith(".") } ?: false
                    file.isFile -> file.name?.let { name ->
                        name.substringAfterLast('.', "").lowercase() in allowedExtensions
                    } ?: false

                    else -> false
                }
            }.flatMap { file ->
                if (file.isDirectory) scanDirectory(file) else listOf(file)
            }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error scanning directory: ${directory.name}", e)
            emptyList()
        }
    }

    fun toggleBookSelection(book: Book) {
        _selectedBooks.value = if (_selectedBooks.value.contains(book)) {
            _selectedBooks.value - book
        } else {
            _selectedBooks.value + book
        }
        _selectionMode.value = _selectedBooks.value.isNotEmpty()
    }

    fun selectAllBooks(books: List<Book>){
        _selectedBooks.value = books
    }


    fun clearBookSelection() {
        _selectedBooks.value = emptyList()
        _selectionMode.value = false
    }



    private suspend fun addNewBook(documentFile: DocumentFile) {
        withContext(Dispatchers.IO) {
            try {
                val bookUriString = documentFile.uri.toString()
                if (!getBookUrisUseCase().contains(bookUriString)) {
                    val book = getBookInfo(documentFile)
                    // Add retry mechanism for database operations
                    retry(attempts = 3) {
                        insertBookUseCase(book)
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error adding book: ${documentFile.name}", e)
                throw e
            }
        }
    }

    private suspend fun <T> retry(
        attempts: Int,
        delayBetweenAttempts: Long = 1000L,
        block: suspend () -> T
    ): T {
        var lastException: Exception? = null
        repeat(attempts) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                lastException = e
                if (attempt < attempts - 1) {
                    delay(delayBetweenAttempts)
                }
            }
        }
        throw lastException ?: IllegalStateException("Retry failed")
    }

    private suspend fun getBookInfo(documentFile: DocumentFile): Book =
        withContext(Dispatchers.IO) {
            try {
                val url = documentFile.uri.toAbsoluteUrl()
                val fileType = when {
                    documentFile.name?.endsWith(".pdf", ignoreCase = true) == true -> FileType.PDF
                    documentFile.name?.let {
                        it.endsWith(".mp3", ignoreCase = true) ||
                                it.endsWith(".m4a", ignoreCase = true) ||
                                it.endsWith(".m4b", ignoreCase = true) ||
                                it.endsWith(".aac", ignoreCase = true)
                    } == true -> FileType.AUDIOBOOK

                    else -> FileType.EPUB
                }

                when (fileType) {
                    FileType.EPUB -> {
                        val asset = url?.let { it ->
                            assetRetriever.retrieve(it).getOrElse { throw ErrorException(it) }
                        }
                        val publication = asset?.let { it ->
                            publicationOpener.open(it, allowUserInteraction = false)
                                .getOrElse { throw ErrorException(it) }
                        }
                        extractEpubBookInfo(publication, documentFile)
                    }

                    FileType.PDF -> extractPdfBookInfo(documentFile)
                    FileType.AUDIOBOOK -> extractAudioBookInfo(documentFile)
                }
            } catch (e: Exception) {
                Book(
                    uri = documentFile.uri.toString(),
                    fileType = when {
                        documentFile.name?.endsWith(
                            ".pdf",
                            ignoreCase = true
                        ) == true -> FileType.PDF

                        documentFile.name?.let {
                            it.endsWith(".mp3", ignoreCase = true) ||
                                    it.endsWith(".m4a", ignoreCase = true) ||
                                    it.endsWith(".m4b", ignoreCase = true) ||
                                    it.endsWith(".aac", ignoreCase = true)
                        } == true -> FileType.AUDIOBOOK

                        else -> FileType.EPUB
                    },
                    title = documentFile.name ?: "Unknown",
                    authors = "",
                    description = null,
                    publishDate = null,
                    publisher = null,
                    language = null,
                    numberOfPages = null,
                    subjects = null,
                    coverPath = null,
                    locator = "",
                )
            }
        }

    private suspend fun extractAudioBookInfo(documentFile: DocumentFile): Book =
        withContext(Dispatchers.IO) {
            val uri = documentFile.uri
            val mediaMetadataRetriever = MediaMetadataRetriever()

            try {
                context.contentResolver.openFileDescriptor(uri, "r")?.use { descriptor ->
                    mediaMetadataRetriever.setDataSource(descriptor.fileDescriptor)

                    val title =
                        mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                            ?: documentFile.name ?: "Unknown"
                    val artist =
                        mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                    val album =
                        mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                    val duration =
                        mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                            ?.toLongOrNull()

                    // Extract cover art
                    val coverArt = mediaMetadataRetriever.embeddedPicture
                    val coverPath = coverArt?.let {
                        saveCoverImage(
                            BitmapFactory.decodeByteArray(
                                it,
                                0,
                                it.size
                            ), documentFile
                        )
                    }

                    Book(
                        uri = uri.toString(),
                        fileType = FileType.AUDIOBOOK,
                        title = title,
                        authors = artist ?: "",
                        description = album,
                        publishDate = null,
                        publisher = null,
                        language = null,
                        numberOfPages = null,
                        subjects = null,
                        coverPath = coverPath,
                        locator = "",
                        duration = duration,
                        narrator = artist
                    )
                } ?: throw IllegalStateException("Unable to open audio file")
            } catch (e: Exception) {
                Book(
                    uri = uri.toString(),
                    fileType = FileType.AUDIOBOOK,
                    title = documentFile.name ?: "Unknown",
                    authors = "",
                    description = null,
                    publishDate = null,
                    publisher = null,
                    language = null,
                    numberOfPages = null,
                    subjects = null,
                    coverPath = null,
                    locator = "",
                )
            } finally {
                mediaMetadataRetriever.release()
            }
        }


    private suspend fun extractPdfBookInfo(documentFile: DocumentFile): Book =
        withContext(Dispatchers.IO) {
            val uri = documentFile.uri
            var pdfRenderer: PdfRenderer? = null

            try {
                context.contentResolver.openFileDescriptor(uri, "r")?.use { descriptor ->
                    pdfRenderer = PdfRenderer(descriptor)

                    val pageCount = pdfRenderer?.pageCount ?: 0
                    val firstPage = pdfRenderer?.openPage(0)

                    // Extract basic info
                    val title = documentFile.name ?: "Unknown"

                    // Generate and save cover image
                    val coverBitmap = firstPage?.let { page ->
                        val bitmap =
                            Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        bitmap
                    }
                    val coverPath = coverBitmap?.let { saveCoverImage(it, documentFile) }

                    firstPage?.close()

                    Book(
                        uri = uri.toString(),
                        fileType = FileType.PDF,
                        title = title,
                        authors = "",
                        description = null,
                        publishDate = null,
                        publisher = null,
                        language = null,
                        numberOfPages = pageCount,
                        subjects = null,
                        coverPath = coverPath,
                        locator = ""
                    )
                } ?: throw IllegalStateException("Unable to open PDF file")
            } catch (e: Exception) {
                // Log the error or handle it as needed
                Book(
                    uri = uri.toString(),
                    fileType = FileType.PDF,
                    title = documentFile.name ?: "Unknown",
                    authors = "",
                    description = null,
                    publishDate = null,
                    publisher = null,
                    language = null,
                    numberOfPages = null,
                    subjects = null,
                    coverPath = null,
                    locator = "",
                )
            } finally {
                pdfRenderer?.close()
            }
        }


    private suspend fun extractEpubBookInfo(
        publication: Publication?,
        documentFile: DocumentFile
    ): Book {
        val coverBitmap = publication?.cover()
        val coverPath = coverBitmap?.let { saveCoverImage(it, documentFile) }

        return Book(
            uri = documentFile.uri.toString(),
            fileType = FileType.EPUB,
            title = publication?.metadata?.title ?: documentFile.name ?: "Unknown",
            authors = publication?.metadata?.authors?.joinToString(", ") { it.name } ?: "",
            description = publication?.metadata?.description,
            publishDate = publication?.metadata?.published?.toString(),
            publisher = publication?.metadata?.publishers?.firstOrNull()?.name,
            language = publication?.metadata?.languages?.firstOrNull(),
            numberOfPages = publication?.metadata?.numberOfPages,
            subjects = publication?.metadata?.subjects?.joinToString(", ") { it.name },
            coverPath = coverPath,
            locator = "",
        )
    }

    private fun saveCoverImage(bitmap: Bitmap, documentFile: DocumentFile): String? {
        var fileName = "${documentFile.name ?: System.currentTimeMillis()}.jpg"
        var file = File(context.filesDir, fileName)
        var counter = 1

        while (file.exists()) {
            fileName = "${documentFile.name ?: System.currentTimeMillis()}_$counter.jpg"
            file = File(context.filesDir, fileName)
            counter++
        }

        return try {
            file.outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                out.flush()
            }
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
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
                        progression = 100f
                    )

                    else -> updatedBook
                }
            }

            updateBookUseCase(updateBook)
        }
    }


    fun sortBooks(sortOption: SortOption, sortOrder: SortOrder) {
        viewModelScope.launch {
            val isAscending = sortOrder == SortOrder.ASCENDING
            val readingStatus = _appPreferences.value.readingStatus
            val fileType = _appPreferences.value.fileTypes
            try {
                getBooksUseCase(sortOption, isAscending, readingStatus, fileType)
                    .cachedIn(viewModelScope)
                    .collect { pagingData ->
                        _books.value = pagingData
                    }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error sorting books: ${e.message}")
            }
        }
    }


    fun filterBooks(option: Any) {
        viewModelScope.launch {
            val currentPreferences = _appPreferences.value
            val newPreferences = when (option) {
                is ReadingStatus -> {
                    val newStatuses = if (option in currentPreferences.readingStatus) {
                        currentPreferences.readingStatus - option
                    } else {
                        currentPreferences.readingStatus + option
                    }
                    currentPreferences.copy(readingStatus = newStatuses)
                }

                is FileType -> {
                    val newFileTypes = if (option in currentPreferences.fileTypes) {
                        emptySet()  // Deselect if it's the only selected option
                    } else {
                        setOf(option)  // Select only this option
                    }
                    currentPreferences.copy(fileTypes = newFileTypes)
                }

                else -> currentPreferences
            }

            updateAppPreferences(newPreferences)
            _appPreferences.value = newPreferences

            getBooksUseCase(
                sortOption = newPreferences.sortBy,
                isAscending = newPreferences.sortOrder == SortOrder.ASCENDING,
                readingStatuses = newPreferences.readingStatus,
                fileTypes = newPreferences.fileTypes
            ).collect { pagingData ->
                _books.value = pagingData
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