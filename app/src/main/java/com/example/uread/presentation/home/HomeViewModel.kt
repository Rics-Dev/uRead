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
import com.example.uread.data.source.local.SharedPreferencesUtil
import com.example.uread.domain.use_case.DeleteBookUseCase
import com.example.uread.domain.use_case.GetBookUrisUseCase
import com.example.uread.domain.use_case.GetBooksUseCase
import com.example.uread.domain.use_case.InsertBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getBooksUseCase: GetBooksUseCase,
    private val getBookUrisUseCase: GetBookUrisUseCase,
    private val insertBookUseCase: InsertBookUseCase,
    private val deleteBookUseCase: DeleteBookUseCase,
    private val assetRetriever: AssetRetriever,
    private val publicationOpener: PublicationOpener,
    application: Application,
    private val sharedPreferencesUtil: SharedPreferencesUtil
) : AndroidViewModel(application) {

    private val appContext: Context = application.applicationContext

    private val _books = MutableStateFlow<List<Book>>(emptyList())

    val books: Flow<PagingData<Book>> = Pager(
        config = PagingConfig(
            pageSize = 9,
            enablePlaceholders = true,
        )
    ) {
        getBooksUseCase()
    }.flow.cachedIn(viewModelScope)



    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    private val _isLoadingNewBooks = MutableStateFlow(false)
    val isLoadingNewBooks = _isLoadingNewBooks.asStateFlow()




    fun initializeApp(onFirstLaunch: () -> Unit) {
        viewModelScope.launch {
            if (sharedPreferencesUtil.isFirstLaunch()) {
                onFirstLaunch()
            } else {
                val uriString = sharedPreferencesUtil.getDirectoryUri(getApplication())
                if (uriString != null) {
                    val uri = Uri.parse(uriString)
                    checkForMissingBooks(uri)
                }
            }
        }
    }


    private suspend fun checkForMissingBooks(uri: Uri) {
        _isLoadingNewBooks.value = true

        // Get existing book URIs from the database directly
        val existingUris = getBookUrisUseCase()
        val booksList = getBooksFromDirectory(appContext, uri)

        // Filter the booksList to only include books not already in the database
        val newBooks = booksList.filter { it.uri.toString() !in existingUris }

        // Add only the missing books
        newBooks.forEach { documentFile ->
            addNewBook(documentFile)
        }

        _isLoadingNewBooks.value = false
    }

    // for first time launch
    fun handleDirectorySelection(uri: Uri) {
        viewModelScope.launch {
            _isLoadingNewBooks.value = true
            checkForMissingBooks(uri)
            _isLoadingNewBooks.value = false
            sharedPreferencesUtil.saveDirectoryUri(uri.toString())
            sharedPreferencesUtil.setFirstLaunch(false)
            sharedPreferencesUtil.saveLastUpdateTime(appContext, System.currentTimeMillis())
        }
    }


    private suspend fun addNewBook(documentFile: DocumentFile) {
        val book = getBookInfo(documentFile)
        _books.value += book
        _snackbarMessage.value = "Adding new book: ${documentFile.name}"
        insertBookUseCase(book)
    }

    private suspend fun getBookInfo(documentFile: DocumentFile): Book = withContext(Dispatchers.IO) {
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
                lastModified = documentFile.lastModified()
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
            lastModified = documentFile.lastModified()
        )
    }

    private fun saveCoverToFile(bitmap: Bitmap, uri: String): String {
        val file = File(appContext.filesDir, "covers/${uri.hashCode()}.png")
        file.parentFile?.mkdirs()
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
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