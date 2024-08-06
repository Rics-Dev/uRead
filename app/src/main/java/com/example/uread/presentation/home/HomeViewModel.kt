package com.example.uread.presentation.home

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.example.uread.data.model.Book
import com.example.uread.data.source.local.BookDao
import com.example.uread.data.source.local.DataStoreUtil
import com.example.uread.data.source.local.SharedPreferencesUtil
import com.example.uread.domain.use_case.DeleteBookUseCase
import com.example.uread.domain.use_case.GetBookUrisUseCase
import com.example.uread.domain.use_case.GetBooksUseCase
import com.example.uread.domain.use_case.InsertBookUseCase
import com.example.uread.pagingSource.BookPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.services.cover
import org.readium.r2.shared.util.ErrorException
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.getOrElse
import org.readium.r2.shared.util.http.DefaultHttpClient
import org.readium.r2.shared.util.toAbsoluteUrl
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bookDao: BookDao,
    private val getBooksUseCase: GetBooksUseCase,
    private val getBookUrisUseCase: GetBookUrisUseCase,
    private val insertBookUseCase: InsertBookUseCase,
    private val deleteBookUseCase: DeleteBookUseCase,
    private val assetRetriever: AssetRetriever,
    private val publicationOpener: PublicationOpener,
    private val dataStoreUtil: DataStoreUtil,
    application: Application,
    private val sharedPreferencesUtil: SharedPreferencesUtil
) : AndroidViewModel(application) {

    private val appContext: Context = application.applicationContext

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    private val _bookCache = MutableStateFlow<Map<String, Book>>(emptyMap()) // Cache for loaded books

    val books: Flow<PagingData<Book>> = Pager(
        config = PagingConfig(
            pageSize = 9,
            enablePlaceholders = true,
        )
    ) {
        getBooksUseCase()
    }.flow.cachedIn(viewModelScope)

//    val books: Flow<PagingData<Book>> = Pager(
//        config = PagingConfig(
//            pageSize = 9,
//            enablePlaceholders = false,
//            maxSize = 100
//        )
//    ) {
//        BookPagingSource(bookDao)
//    }.flow.cachedIn(viewModelScope)



    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    private val _isLoadingNewBooks = MutableStateFlow(false)
    val isLoadingNewBooks = _isLoadingNewBooks.asStateFlow()




    fun initializeApp(onFirstLaunch: () -> Unit) {
        viewModelScope.launch {
            dataStoreUtil.getAppState().collect { (isFirstLaunch, uriString) ->
                if (isFirstLaunch) {
                    onFirstLaunch()
                } else {
                    uriString?.let {
                        val uri = Uri.parse(it)
                        checkForMissingBooks(uri)
                    } ?: onFirstLaunch() // If uriString is null, treat as first launch
                }
            }
        }
    }


    // for first time launch
    fun handleDirectorySelection(uri: Uri) {
        viewModelScope.launch {
            if (!isDirectoryAlreadySet(uri)) {
                _isLoadingNewBooks.value = true
                checkForMissingBooks(uri)
                dataStoreUtil.saveDirectoryUri(uri.toString())
                dataStoreUtil.setFirstLaunch(false)
                _isLoadingNewBooks.value = false
            }
        }
    }

    private suspend fun isDirectoryAlreadySet(newUri: Uri): Boolean {
        return dataStoreUtil.getDirectoryUri().firstOrNull() == newUri.toString()
    }


    private suspend fun checkForMissingBooks(uri: Uri) {
        _isLoadingNewBooks.value = true

        // Get existing book URIs from the database directly
        val existingUris = getBookUrisUseCase()
        val booksList = getBooksFromDirectory(appContext, uri)

        // Filter the booksList to only include books not already in the database
//        val newBooks = booksList.filter { it.uri.toString() !in existingUris }

        val newBooks = booksList.filter { documentFile ->
            val bookUriString = documentFile.uri.toString()
            bookUriString !in existingUris && bookUriString !in _bookCache.value.keys
        }


        // Add only the missing books
        newBooks.forEach { documentFile ->
            addNewBook(documentFile)
        }

        _isLoadingNewBooks.value = false
    }





    private suspend fun addNewBook(documentFile: DocumentFile) {
        val book = getBookInfo(documentFile)
        _books.value += book
        _snackbarMessage.value = "Adding new book: ${documentFile.name}"
        insertBookUseCase(book)

        _bookCache.value += (book.uri to book)
    }

     suspend fun getBookInfo(documentFile: DocumentFile): Book = withContext(Dispatchers.IO) {
        try {
            val url = documentFile.uri.toAbsoluteUrl()
            val asset = url?.let { it -> assetRetriever.retrieve(it).getOrElse { throw ErrorException(it) } }
            val publication = asset?.let { it -> publicationOpener.open(it, allowUserInteraction = false).getOrElse { throw ErrorException(it) } }
            extractBookInfo(publication, documentFile)
        } catch (e: Exception) {
            Book(
                uri = documentFile.uri.toString(),
                title = documentFile.name ?: "Unknown",
                authors = "",
                description = null,
                coverPath = null,
                locator = "",
            )
        }
    }

    private suspend fun extractBookInfo(publication: Publication?, documentFile: DocumentFile): Book {
        val coverBitmap = publication?.cover()
        val coverPath = coverBitmap?.let { saveCoverToFile(it, documentFile.uri.toString()) }

        return Book(
            uri = documentFile.uri.toString(),
            title = publication?.metadata?.title ?: documentFile.name ?: "Unknown Title",
            authors = publication?.metadata?.authors?.joinToString(",") { it.name } ?: "",
            description = publication?.metadata?.description,
            coverPath = coverPath,
            locator = "",
        )
    }

    private fun saveCoverToFile(bitmap: Bitmap, uri: String): String {
        val file = File(appContext.filesDir, "covers/${uri.hashCode()}.png")
        file.parentFile?.mkdirs()
        file.outputStream().use { out ->
            if (!bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 90, out)) {
                throw IOException("Failed to save cover")
            }
        }
        return file.absolutePath
    }



    //retrieve epub files from the directory
    private fun getBooksFromDirectory(context: Context, uri: Uri): List<DocumentFile> {
        val booksList = mutableListOf<DocumentFile>()
        val treeUri = DocumentFile.fromTreeUri(context, uri)
        treeUri?.listFiles()?.forEach { documentFile ->
            if (documentFile.isFile && documentFile.name?.endsWith(".epub", ignoreCase = true) == true) {
                booksList.add(documentFile)
            }
        }
        return booksList
    }


}