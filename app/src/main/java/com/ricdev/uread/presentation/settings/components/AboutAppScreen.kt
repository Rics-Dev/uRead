package com.ricdev.uread.presentation.settings.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.mikepenz.markdown.m3.Markdown
import com.ricdev.uread.R
import com.ricdev.uread.presentation.settings.viewmodels.AboutViewModel
import com.ricdev.uread.util.customMarkdownTypography
import com.ricdev.uread.util.getAppVersion
import java.io.IOException


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutAppScreen(
    navController: NavController,
    viewModel: AboutViewModel = hiltViewModel()
) {


    val isDarkTheme = viewModel.isDarkTheme.collectAsStateWithLifecycle()

    val elevationOverlay = if (isDarkTheme.value == true) {
        Color.White.copy(alpha = 0.09f)
            .compositeOver(MaterialTheme.colorScheme.surface)
    } else {
        MaterialTheme.colorScheme.surface
    }




    val context = LocalContext.current
    val appVersion = getAppVersion()


    fun readPrivacyPolicy(context: Context): String {
        return try {
            context.assets.open("documentation/PRIVACY_POLICY.md").bufferedReader()
                .use { it.readText() }
        } catch (e: IOException) {
            "Error loading privacy policy"
        }
    }

    fun readChangelog(context: Context): String {
        return try {
            context.assets.open("documentation/CHANGELOG.md").bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            "Error loading changelog"
        }
    }

    val privacyPolicy = remember { readPrivacyPolicy(context) }
    val changelog = remember { readChangelog(context) }


    var showVersionDialog by remember { mutableStateOf(false) }
    var showWhatsNewModal by remember { mutableStateOf(false) }
    var showPrivacyPolicyModal by remember { mutableStateOf(false) }
    var showLibrariesModal by remember { mutableStateOf(false) }




    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = { Text(text = stringResource(R.string.about)) },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            item {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-12).dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(150.dp)
                        )
                        Text(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            color = Color(0xE6564026),
                            textAlign = TextAlign.Start,
                            text = " uRead",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .offset(y = (-28).dp)
                        )
                        Text(
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily.SansSerif,
                            color = Color(0xE6564026),
                            textAlign = TextAlign.Start,
                            text = "v${appVersion?.versionName ?: "Unknown"}",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier
                                .offset(y = (-28).dp)
                        )
                    }
                }

                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
            }


            item {
                ListItem(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = if (!isDarkTheme.value!!) {
                                Color.Black.copy(alpha = 0.8f)
                            } else {
                                Color.Black.copy(alpha = 0.5f)
                            }
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .background(elevationOverlay)
                        .clickable(onClick = {
                            showVersionDialog = true
                        })
                        .fillMaxWidth(),
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.version),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    leadingContent = {
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = "Rating",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent,
                    )
                )
            }


            item {

                ListItem(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = if (!isDarkTheme.value!!) {
                                Color.Black.copy(alpha = 0.8f)
                            } else {
                                Color.Black.copy(alpha = 0.5f)
                            }
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .background(elevationOverlay)
                        .clickable(onClick = {
                            showWhatsNewModal = true
                        })
                        .fillMaxWidth(),
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.what_s_new),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    leadingContent = {
                        Icon(
                            Icons.Outlined.Update,
                            contentDescription = "What's new",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent,
                    )
                )
            }


            item {

                ListItem(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = if (!isDarkTheme.value!!) {
                                Color.Black.copy(alpha = 0.8f)
                            } else {
                                Color.Black.copy(alpha = 0.5f)
                            }
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .background(elevationOverlay)
                        .clickable(onClick = {
                            showPrivacyPolicyModal = true
                        })
                        .fillMaxWidth(),
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.privacy_policy),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    leadingContent = {
                        Icon(
                            Icons.Outlined.PrivacyTip,
                            contentDescription = "Rating",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent,
                    )
                )
            }



            item {

                ListItem(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = if (!isDarkTheme.value!!) {
                                Color.Black.copy(alpha = 0.8f)
                            } else {
                                Color.Black.copy(alpha = 0.5f)
                            }
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .background(elevationOverlay)
                        .clickable(onClick = {
                            showLibrariesModal = true
                        })
                        .fillMaxWidth(),
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.libraries),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    leadingContent = {
                        Icon(
                            Icons.Outlined.Book,
                            contentDescription = "Libraries",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent,
                    )
                )
                Spacer(modifier = Modifier.height(48.dp))

            }






            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    SocialMediaButton(
                        icon = painterResource(id = R.drawable.github),
                        contentDescription = "GitHub",
                        onClick = {
                            try {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://github.com/Rics-Dev/uRead")
                                )
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Unable to open GitHub", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    )
                }
            }


        }
    }



    if (showVersionDialog) {
        AlertDialog(
            onDismissRequest = {
                showVersionDialog = false
            },
            title = {
                Text(text = stringResource(R.string.version))
            },
            text = {
                Column {
                    Text(text = "uRead v${appVersion?.versionName ?: "Unknown"} (${appVersion?.versionNumber ?: "Unknown"}) stable")
                    Text(text = "Release Date: ${appVersion?.releaseDate ?: "Unknown"}")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showVersionDialog = false
                    }
                ) {
                    Text(text = "OK")
                }
            }
        )
    }




    if (showWhatsNewModal) {
        ModalBottomSheet(
            shape = BottomSheetDefaults.HiddenShape,
            dragHandle = null,
            onDismissRequest = { showWhatsNewModal = false },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { it != SheetValue.PartiallyExpanded }
            ),
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)

                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Markdown(
                        typography = customMarkdownTypography(),
                        content = changelog
                    )
                }

                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://github.com/Rics-Dev/uRead/blob/main/app/src/main/assets/documentation/CHANGELOG.md")
                                )
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Unable to open Changelog",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Navigate to")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            showWhatsNewModal = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Close")
                    }
                }
            }
        }
    }






    if (showPrivacyPolicyModal) {
        ModalBottomSheet(
            shape = BottomSheetDefaults.HiddenShape,
            dragHandle = null,
            onDismissRequest = { showPrivacyPolicyModal = false },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { it != SheetValue.PartiallyExpanded }
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .weight(1f) // Give the scrollable content as much space as possible
                        .verticalScroll(rememberScrollState())
                ) {
                    Markdown(
                        typography = customMarkdownTypography(),
                        content = privacyPolicy
                    )
                }
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://github.com/Rics-Dev/uRead/blob/main/app/src/main/assets/documentation/PRIVACY_POLICY.md")
                                )
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Unable to open Privacy Policy",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Navigate to")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            showPrivacyPolicyModal = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Close")
                    }
                }
            }
        }
    }


    if (showLibrariesModal) {
        ModalBottomSheet(
            shape = BottomSheetDefaults.HiddenShape,
            dragHandle = null,
            onDismissRequest = { showLibrariesModal = false },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { it != SheetValue.PartiallyExpanded }
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(top = 16.dp)

            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp)
                ) {
                    Text("Libraries", style = MaterialTheme.typography.headlineSmall)
                }


                Box(
                    modifier = Modifier
                        .weight(1f) // Give the scrollable content as much space as possible
                ) {
                    LibrariesContainer(
                        Modifier.fillMaxSize()
                    )
                }



                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Button(
                        onClick = {
                            showLibrariesModal = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Close")
                    }
                }
            }

        }
    }


}


@Composable
fun SocialMediaButton(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit
) {
    ElevatedButton(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        modifier = Modifier.size(48.dp),
        contentPadding = PaddingValues(0.dp),
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp)
        )
    }
}

