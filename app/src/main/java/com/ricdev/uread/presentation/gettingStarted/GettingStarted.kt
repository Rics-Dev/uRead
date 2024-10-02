package com.ricdev.uread.presentation.gettingStarted

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.ricdev.uread.R
import com.ricdev.uread.navigation.Screens
import com.ricdev.uread.presentation.gettingStarted.components.ActionButton
import com.ricdev.uread.presentation.gettingStarted.components.StorageAccessDialog

@Composable
fun GettingStartedScreen(
    navController: NavHostController,
    viewModel: GettingStartedViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val appPreferences by viewModel.appPreferences.collectAsStateWithLifecycle()
    val isButtonsEnabled by viewModel.isButtonsEnabled.collectAsStateWithLifecycle()
    var showSelectDirectoryDialog by remember { mutableStateOf(false) }
//    var showStoragePermissionDialog by remember { mutableStateOf(false) }

    val getDirectoryPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
            uri?.let {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                val updatedDirectories = appPreferences.scanDirectories + it.toString()
                viewModel.updateAppPreferences(
                    appPreferences.copy(
                        isFirstLaunch = false,
                        scanDirectories = updatedDirectories
                    )
                )
                navController.popBackStack()
                navController.navigate(Screens.HomeScreen.route)
            }
        }


    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .animateContentSize()
                    .semantics {
                        contentDescription = "Getting Started Screen"
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var visible by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    visible = true  // Trigger animations when the composable is first displayed
                }

                val slideInAnimationSpec = tween<IntOffset>(durationMillis = 500)
                val tweenInAnimationSpec = tween<Float>(durationMillis = 1000)


                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tweenInAnimationSpec) + slideInVertically(
                        animationSpec = slideInAnimationSpec,
                        initialOffsetY = { it })
                ) {
                    Image(
                        painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(150.dp)
                            .semantics { contentDescription = "uRead app logo" }
                    )

                }
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tweenInAnimationSpec) + slideInVertically(
                        slideInAnimationSpec,
                        initialOffsetY = { it })
                ) {
                    Text(
                        text = stringResource(R.string.welcome_to_uread),
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.semantics {
                            contentDescription = "Welcome to uRead heading"
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tweenInAnimationSpec) + slideInVertically(
                        slideInAnimationSpec,
                        initialOffsetY = { it })
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(R.string.to_get_started_please_select_a_directory_where_you_would_like_to_load_your_books),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tweenInAnimationSpec) + slideInVertically(
                        slideInAnimationSpec,
                        initialOffsetY = { it })
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(R.string.you_can_edit_this_later),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tweenInAnimationSpec) + slideInVertically(
                        slideInAnimationSpec,
                        initialOffsetY = { it / 2 })
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ActionButton(
                            modifier = Modifier.weight(1f),
                            text = stringResource(R.string.select_directory),
                            icon = Icons.Filled.FolderOpen,
                            enabled = isButtonsEnabled,
                            onClick = { showSelectDirectoryDialog = true },
                            description = "Select directory button"
                        )
                    }
                }
            }

            TextButton(
                onClick = {
                    viewModel.skipGettingStarted()
                    navController.popBackStack()
                    navController.navigate(Screens.HomeScreen.route)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .semantics { contentDescription = "Skip getting started" }
            ) {
                Text(
                    text = stringResource(R.string.skip),
                    style = MaterialTheme.typography.labelLarge
                )
            }


        }

        if (showSelectDirectoryDialog) {
            StorageAccessDialog(
                title = stringResource(R.string.select_scan_directory),
                message = stringResource(R.string.please_select_a_directory_where_your_ebooks_are_stored_you_can_edit_this_later_in_settings),
                confirmButtonText = stringResource(R.string.select),
                onConfirm = {
                    showSelectDirectoryDialog = false
                    getDirectoryPermissionLauncher.launch(null)
                },
                onDismiss = { showSelectDirectoryDialog = false },
            )
        }

//        if (showStoragePermissionDialog) {
//            StorageAccessDialog(
//                title = stringResource(R.string.storage_permission),
//                message = stringResource(R.string.this_app_needs_access_to_all_files_on_your_device_to_scan_for_epub_books),
//                confirmButtonText = "Allow",
//                onConfirm = {
//                    showStoragePermissionDialog = false
////                    viewModel.handleStoragePermissionResult(getStoragePermissionLauncher)
//                },
//                onDismiss = {
//                    showStoragePermissionDialog = false
//                    viewModel.updateEnabledButton(true)
//                },
//            )
//        }
    }
}

