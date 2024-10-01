package com.ricdev.uread.presentation.onlineBooks

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.ricdev.uread.util.SetFullScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(
    navController: NavHostController,
    url: String,
    viewModel: WebViewScreenViewModel = hiltViewModel(),
) {

    val context = LocalContext.current
    val activity = context as Activity


    val appPreferences by viewModel.appPreferences.collectAsStateWithLifecycle()
    val isDownloading by viewModel.isDownloading.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    var webView by remember { mutableStateOf<WebView?>(null) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    var loadingProgress by remember { mutableFloatStateOf(0.1f) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showDirectoryDialog by remember { mutableStateOf(false) }
    var downloadUrl by remember { mutableStateOf<String?>(null) }

    SetFullScreen(context, showSystemBars = false)


    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbarMessage()
        }
    }


//    LaunchedEffect(Unit) {
//        if (ContextCompat.checkSelfPermission(
//                context,
//                android.Manifest.permission.POST_NOTIFICATIONS
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                ActivityCompat.requestPermissions(
//                    activity,
//                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
//                    1,
//                )
//            }
//        }
//    }





    val webViewClient = remember {
        object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                val newUrl = request.url.toString()
                view.loadUrl(newUrl)
                return true
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                loadingProgress = 0.1f
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                canGoBack = view.canGoBack()
                canGoForward = view.canGoForward()
                loadingProgress = 1f
            }
        }
    }


    val webChromeClient = remember {
        object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                loadingProgress = newProgress / 100f
            }
        }
    }








    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WebView") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (!isDownloading) {
                                navController.popBackStack()
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Please wait for the download to complete")
                                }
                            }
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to app"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { webView?.goBack() },
                        enabled = canGoBack
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back")
                    }
                    IconButton(
                        onClick = { webView?.goForward() },
                        enabled = canGoForward
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Go forward"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LinearProgressIndicator(
                progress = { loadingProgress },
                modifier = Modifier.fillMaxWidth(),
            )
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
//                        settings.javaScriptEnabled = true
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                        this.webViewClient = webViewClient
                        this.webChromeClient = webChromeClient
                        this.setDownloadListener { url, _, _, _, _ ->
                            downloadUrl = url
                            showDirectoryDialog = true
                        }
                        webView = this
                    }
                },
                update = { webView ->
                    webView.loadUrl(url)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val sheetMaxWidth = screenWidthDp * 0.95f
    var selectedDownloadDirectory by remember { mutableStateOf("") }

    if (showDirectoryDialog) {
        ModalBottomSheet(
            dragHandle = null,
            modifier = Modifier.padding(bottom = 48.dp),
            contentWindowInsets = { WindowInsets(0) },
            shape = RoundedCornerShape(16.dp),
            sheetMaxWidth = sheetMaxWidth.dp,
            onDismissRequest = { showDirectoryDialog = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = "Download")
                    Text("Select download destination", style = MaterialTheme.typography.titleLarge)
                }
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .height(150.dp)
                ) {
                    items(appPreferences.scanDirectories.toList()) { directory ->
                        val uri = Uri.parse(directory)
                        val directoryName = uri.lastPathSegment?.substringAfter(":") ?: directory

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = selectedDownloadDirectory == directory,
                                onCheckedChange = { isChecked ->
                                    if (isChecked) {
                                        selectedDownloadDirectory = directory
                                    } else if (selectedDownloadDirectory == directory) {
                                        selectedDownloadDirectory = ""
                                    }
                                },
                            )
                            Text(directoryName)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (selectedDownloadDirectory.isNotEmpty()) {
                            downloadUrl?.let {
                                viewModel.startDownload(
                                    selectedDownloadDirectory,
                                    it
                                )
                            }
                            showDirectoryDialog = false
                            selectedDownloadDirectory = ""
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Please select a download directory.")
                            }
                        }
                    }) {
                    Text("Download")
                }
            }
        }
    }
}


//val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
