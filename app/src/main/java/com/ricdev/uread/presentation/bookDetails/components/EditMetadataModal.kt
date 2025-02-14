package com.ricdev.uread.presentation.bookDetails.components

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.data.model.FileType
import com.ricdev.uread.presentation.bookDetails.BookDetailsViewModel
import com.ricdev.uread.util.PermissionHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMetadataModal(
    book: Book?,
    viewModel: BookDetailsViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
) {
    if (book == null) return

    val context = LocalContext.current
    val updateError by viewModel.updateError.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf(book.title) }
    var titleError by remember { mutableStateOf(false) }

    var coverImage by remember { mutableStateOf(book.coverPath) }

    var authors by remember { mutableStateOf(book.authors) }
    var authorsError by remember { mutableStateOf(false) }

    var description by remember { mutableStateOf(book.description ?: "") }
    var publishDate by remember { mutableStateOf(book.publishDate ?: "") }
    var publisher by remember { mutableStateOf(book.publisher ?: "") }
    var language by remember { mutableStateOf(book.language ?: "") }
    var numberOfPages by remember { mutableStateOf(book.numberOfPages?.toString() ?: "") }
    var subjects by remember { mutableStateOf(book.subjects ?: "") }
    var narrator by remember { mutableStateOf(book.narrator ?: "") }

    // Content picker launcher
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val coverPath = viewModel.updateCoverImage(context, uri)
            if (coverPath != null) {
                coverImage = coverPath
            }
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.READ_MEDIA_IMAGES, false) ||
                    permissions.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE, false) -> {
                imagePicker.launch("image/*")
            }
        }
    }


    ModalBottomSheet(
        shape = BottomSheetDefaults.HiddenShape,
        dragHandle = null,
        onDismissRequest = onDismiss,
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
                .padding(vertical = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = "Edit Book",
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    Box(
                        modifier = Modifier
                            .height(200.dp)
                            .aspectRatio(2f / 3f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(
                                onClick = {
                                    if (PermissionHandler.hasPermissions(context)) {
                                        imagePicker.launch("image/*")
                                    } else {
                                        PermissionHandler.requestPermissions(permissionLauncher)
                                    }
                                }
                            )
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(coverImage),
                            contentDescription = "Book cover",
                            modifier = Modifier.fillMaxSize()
                        )
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Add icon",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it; titleError = false },
                        label = { Text("Title *") },
                        isError = titleError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (titleError) Text("Required", color = MaterialTheme.colorScheme.error)

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = authors,
                    onValueChange = { authors = it; authorsError = false },
                    label = { Text("Authors *") },
                    isError = authorsError,
                    modifier = Modifier.fillMaxWidth()
                    )
                    if (authorsError) Text("Required", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                          value = publishDate,
                    onValueChange = { publishDate = it },
                    label = { Text("Publication Date") },
                    placeholder = { Text("YYYY-MM-DD") },
                    modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                          value = publisher,
                    onValueChange = { publisher = it },
                    label = { Text("Publisher") },
                    modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
           value = language,
                    onValueChange = { language = it },
                    label = { Text("Language") },
                    modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = numberOfPages,
                        onValueChange = { numberOfPages = it },
                        label = { Text("Pages") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = subjects,
                        onValueChange = { subjects = it },
                        label = { Text("Subjects (comma-separated)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (book.fileType == FileType.AUDIOBOOK) {
                        OutlinedTextField(
                            value = narrator,
                            onValueChange = { narrator = it },
                            label = { Text("Narrator") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            if (updateError != null) {
                Text(
                    text = updateError!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        titleError = title.isBlank()
                        authorsError = authors.isBlank()
                        if (titleError || authorsError) return@Button

                        viewModel.updateBook(
                            book.copy(
                                title = title.trim(),
                                coverPath = coverImage,
                                authors = authors.trim(),
                                description = description.ifEmpty { null },
                                publishDate = publishDate.ifEmpty { null },
                                publisher = publisher.ifEmpty { null },
                                language = language.ifEmpty { null },
                                numberOfPages = numberOfPages.toIntOrNull(),
                                subjects = subjects.ifEmpty { null },
                                narrator = if (book.fileType == FileType.AUDIOBOOK) narrator.ifEmpty { null } else null
                            )
                        )
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Save")
                }
            }
        }
    }


}