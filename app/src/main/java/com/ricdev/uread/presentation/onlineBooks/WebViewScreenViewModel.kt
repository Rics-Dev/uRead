package com.ricdev.uread.presentation.onlineBooks

//import android.content.Context
//import android.net.Uri
//import android.os.Environment
//import android.util.Log
//import androidx.documentfile.provider.DocumentFile
//import androidx.lifecycle.viewModelScope
//import com.ketch.Ketch
//import com.ketch.Status
//import com.ricdev.uread.data.model.AppPreferences
//import com.ricdev.uread.util.event.AppEvent
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import java.io.File
//import java.io.IOException
import javax.inject.Inject
import android.app.Application
import com.ricdev.uread.util.event.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import com.ricdev.uread.data.source.local.AppPreferencesUtil
import androidx.lifecycle.AndroidViewModel


@HiltViewModel
class WebViewScreenViewModel @Inject constructor(
    private val appPreferencesUtil: AppPreferencesUtil,
    private val eventBus: EventBus,
    application: Application,
) : AndroidViewModel(application) {
//
//    private val context: Context
//        get() = getApplication<Application>().applicationContext
//
//    private val ketch = Ketch.builder()
////        .setNotificationConfig(
////            NotificationConfig(
////                enabled = true,
////                smallIcon = R.mipmap.ic_launcher_foreground
////            )
////        )
//        .build(context)
//
//
//    private val _appPreferences = MutableStateFlow(AppPreferencesUtil.defaultPreferences)
//    val appPreferences: StateFlow<AppPreferences> = _appPreferences.asStateFlow()
//
//    private val _isDownloading = MutableStateFlow(false)
//    val isDownloading: StateFlow<Boolean> = _isDownloading.asStateFlow()
//
//    private val _snackbarMessage = MutableStateFlow<String?>(null)
//    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()
//
//    init {
//        viewModelScope.launch {
//            appPreferencesUtil.appPreferencesFlow.collect { preferences ->
//                _appPreferences.value = preferences
//            }
//        }
//    }
//
//
//
//
//    fun startDownload(selectedDirectory: String, downloadUrl: String) {
//        val uri = Uri.parse(selectedDirectory)
//        val documentFile = DocumentFile.fromTreeUri(context, uri)
//
//        if (documentFile != null && documentFile.isDirectory) {
//            val downloadsDirectory =
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                    .toString()
//            val id = ketch.download(
//                url = downloadUrl,
//                path = downloadsDirectory
//            )
//
//            _isDownloading.value = true
//
//            viewModelScope.launch {
//                ketch.observeDownloadById(id).collect { downloadModel ->
//                    when (downloadModel.status) {
//                        Status.SUCCESS -> {
//                            showSnackbar("Download completed")
//                            val downloadedFile = File(downloadsDirectory, downloadModel.fileName)
//                            val newFile = documentFile.createFile(
//                                "application/epub+zip",
//                                downloadModel.fileName
//                            )
//                            if (newFile != null) {
//                                copyFileToUri(downloadedFile, newFile.uri)
//                                eventBus.emitEvent(AppEvent.RefreshBooks)
//                                if (downloadedFile.delete()) {
//                                    Log.d("File Move", "File moved to ${newFile.uri}")
//                                } else {
//                                    Log.e(
//                                        "File Delete Error",
//                                        "Failed to delete original file ${downloadedFile.path}"
//                                    )
//                                }
//                            } else {
//                                Log.e(
//                                    "File Creation Error",
//                                    "Failed to create file in ${documentFile.uri}"
//                                )
//                            }
//                            _isDownloading.value = false
//                        }
//                        Status.FAILED -> {
//                            showSnackbar("Download failed: ${downloadModel.failureReason}")
//                            _isDownloading.value = false
//                        }
//                        Status.STARTED -> showSnackbar("Download started")
//                        Status.PAUSED -> showSnackbar("Download paused")
//                        Status.CANCELLED -> showSnackbar("Download cancelled")
//                        Status.DEFAULT -> showSnackbar("Download default state")
//                        Status.PROGRESS -> {
//                            val progressMessage = if (downloadModel.total == -1L) {
//                                "Downloading ${downloadModel.fileName} (size unknown)"
//                            } else {
//                                "Downloaded ${downloadModel.progress}% of ${downloadModel.total} bytes"
//                            }
//                            Log.d("Download Progress", progressMessage)
//                            showSnackbar(progressMessage)
//                        }
//                        Status.QUEUED -> {
//                            showSnackbar("Download queued")
//                        }
//                    }
//                }
//            }
//        } else {
//            Log.e("DocumentFile Error", "DocumentFile is null or cannot write to $uri")
//            showSnackbar("Error: Unable to access the selected directory")
//        }
//    }
//
//    private fun copyFileToUri(sourceFile: File, targetUri: Uri) {
//        try {
//            context.contentResolver.openOutputStream(targetUri)?.use { outputStream ->
//                sourceFile.inputStream().use { inputStream ->
//                    inputStream.copyTo(outputStream)
//                }
//            }
//            Log.d("File Copy", "File copied to $targetUri")
//        } catch (e: IOException) {
//            Log.e("File Copy Error", "Failed to copy file to $targetUri", e)
//            showSnackbar("Error: Failed to copy file to the selected directory")
//        }
//    }
//
//    private fun showSnackbar(message: String) {
//        _snackbarMessage.value = message
//    }
//
//    fun clearSnackbarMessage() {
//        _snackbarMessage.value = null
//    }


}
