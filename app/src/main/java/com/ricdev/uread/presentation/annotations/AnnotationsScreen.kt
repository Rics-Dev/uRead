package com.ricdev.uread.presentation.annotations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.ricdev.uread.data.model.AnnotationType
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.data.model.BookAnnotation
import com.ricdev.uread.presentation.sharedComponents.CustomNavigationDrawer
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.readium.r2.shared.publication.Locator
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextField
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ricdev.uread.R
import com.ricdev.uread.data.model.AppPreferences
//import com.ricsdev.uread.presentation.sharedComponents.PremiumModal
import com.ricdev.uread.util.PurchaseHelper


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnotationsScreen(
    navController: NavHostController,
    purchaseHelper: PurchaseHelper,
    viewModel: AnnotationsViewModel = hiltViewModel()
) {
    val booksWithAnnotations by viewModel.booksWithAnnotations.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()


    val sortedBooksWithAnnotations = booksWithAnnotations.sortedBy { it.book.title }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedBottomTabIndex by remember { mutableIntStateOf(0) }

//    var showPremiumModal by remember { mutableStateOf(false) }

    var showAnnotationDialog by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    var selectedAnnotation by remember {
        mutableStateOf(
            BookAnnotation(
                id = 0,
                bookId = 0,
                note = "",
                locator = "",
                color = Color.Yellow.toArgb().toString(),
                type = AnnotationType.HIGHLIGHT,
            )
        )
    }

    val appPreferences by viewModel.appPreferences.collectAsStateWithLifecycle()

    CustomNavigationDrawer(
        purchaseHelper = purchaseHelper,
        drawerState = drawerState,
        navController = navController
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.annotations)) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { isSearchActive = !isSearchActive }
                        ) {
                            Icon(
                                if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = "Search Note"
                            )
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Star, contentDescription = "Highlights") },
                        label = { Text(stringResource(R.string.highlights)) },
                        selected = selectedBottomTabIndex == 0,
                        onClick = { selectedBottomTabIndex = 0 }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Edit, contentDescription = "Underlines") },
                        label = { Text(stringResource(R.string.underlines)) },
                        selected = selectedBottomTabIndex == 1,
                        onClick = { selectedBottomTabIndex = 1 }
                    )
                }
            }
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedVisibility(visible = isSearchActive) {
                    // Search bar
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        placeholder = { Text(stringResource(R.string.search_annotations)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") }
                    )
                }


                if (sortedBooksWithAnnotations.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            stringResource(R.string.no_annotations_found),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            stringResource(R.string.start_adding_highlights_or_underlines_to_your_books),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        ScrollableTabRow(
                            selectedTabIndex = selectedTabIndex,
                            edgePadding = 16.dp
                        ) {
                            sortedBooksWithAnnotations.forEachIndexed { index, bookWithAnnotations ->
                                Tab(
                                    selected = selectedTabIndex == index,
                                    onClick = { selectedTabIndex = index },
                                    text = {
                                        Text(
                                            text = bookWithAnnotations.book.title,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                )
                            }
                        }

                        val filteredAnnotations = when (selectedBottomTabIndex) {
                            0 -> sortedBooksWithAnnotations[selectedTabIndex].annotation.filter {
                                it.type == AnnotationType.HIGHLIGHT &&
                                        it.note?.contains(searchQuery, ignoreCase = true) == true
                            }
                            1 -> sortedBooksWithAnnotations[selectedTabIndex].annotation.filter {
                                it.type == AnnotationType.UNDERLINE &&
                                        it.note?.contains(searchQuery, ignoreCase = true) == true
                            }
                            else -> emptyList()
                        }

                        BookAnnotationContent(
                            appPreferences = appPreferences,
                            bookWithAnnotations = BookWithAnnotations(
                                sortedBooksWithAnnotations[selectedTabIndex].book,
                                filteredAnnotations
                            ),
                            onAnnotationClick = { annotation ->
                                showAnnotationDialog = true
                                selectedAnnotation = annotation
                            },
                            onUpdateAnnotation = { updatedAnnotation ->
                                viewModel.updateAnnotation(
                                    updatedAnnotation
                                )
                            },
                            onRemoveAnnotation = { annotation ->
                                viewModel.removeAnnotation(
                                    annotation
                                )
                            },

                            showPremiumModal = {
                                viewModel.purchasePremium(purchaseHelper)
                            }
                        )


                        if (showAnnotationDialog) {
                            AlertDialog(
                                onDismissRequest = { showAnnotationDialog = false },
                                title = { Text(text = stringResource(R.string.annotation)) },
                                text = {
                                    Text("${selectedAnnotation.note}")
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            showAnnotationDialog = false
                                        }
                                    ) {
                                        Text(stringResource(R.string.close))
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }

//    if (showPremiumModal) {
//        PremiumModal(
//            purchaseHelper = purchaseHelper,
//            hidePremiumModal = { showPremiumModal = false }
//        )
//    }
}

@Composable
fun BookAnnotationContent(
    appPreferences: AppPreferences,
    bookWithAnnotations: BookWithAnnotations,
    onAnnotationClick: (BookAnnotation) -> Unit,
    onUpdateAnnotation: (BookAnnotation) -> Unit,
    onRemoveAnnotation: (BookAnnotation) -> Unit,
    showPremiumModal: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(bookWithAnnotations.annotation) { annotation ->
            AnnotationItem(
                appPreferences = appPreferences,
                annotation = annotation,
                onClick = { onAnnotationClick(annotation) },
                onUpdateAnnotation = { updatedAnnotation -> onUpdateAnnotation(updatedAnnotation) },
                onRemoveAnnotation = { removedAnnotation -> onRemoveAnnotation(removedAnnotation) },
                showPremiumModal = { showPremiumModal() }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AnnotationItem(
    appPreferences: AppPreferences,
    annotation: BookAnnotation,
    onClick: () -> Unit,
    onUpdateAnnotation: (BookAnnotation) -> Unit,
    onRemoveAnnotation: (BookAnnotation) -> Unit,
    showPremiumModal: () -> Unit,

    ) {

    var showRemoveAnnotationDialog by remember { mutableStateOf(false) }

    var isPaletteVisible by remember { mutableStateOf(false) }

    var selectedColor by remember {
        mutableStateOf(
            Color(
                annotation.color.toIntOrNull() ?: Color.Yellow.toArgb()
            )
        )
    }
    val controller = rememberColorPickerController()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(8.dp),
                clip = true
            )
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp)


    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            annotation.color
                                .toIntOrNull()
                                ?.let { Color(it) } ?: Color.Yellow)
                        .clickable(onClick = {
                            if (appPreferences.isPremium) {
                                isPaletteVisible = !isPaletteVisible
                            } else {
                                showPremiumModal()
                            }
                        })
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = annotation.note ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { showRemoveAnnotationDialog = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Remove Note")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = Locator.fromJSON(JSONObject(annotation.locator))?.title ?: stringResource(R.string.unknown),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )


            AnimatedVisibility(
                visible = isPaletteVisible,
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.wrapContentSize()
                    ) {
                        HsvColorPicker(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(350.dp)
                                .padding(10.dp),
                            controller = controller,
                            initialColor = selectedColor,
                            onColorChanged = { colorEnvelope ->
                                selectedColor = colorEnvelope.color
                            }
                        )

                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(selectedColor)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                val updatedNote =
                                    annotation.copy(color = selectedColor.toArgb().toString())
                                onUpdateAnnotation(updatedNote)
                                isPaletteVisible = false
                            }
                        ) {
                            Text(stringResource(R.string.select))
                        }
                    }
                }
            }
        }
    }



    if (showRemoveAnnotationDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveAnnotationDialog = false },
            title = { Text(stringResource(R.string.remove_annotation)) },
            text = { Text(stringResource(R.string.are_you_sure_you_want_to_remove_this_annotation)) },
            dismissButton = {
                Button(
                    onClick = { showRemoveAnnotationDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
                    onClick = {
                        onRemoveAnnotation(annotation)
                        showRemoveAnnotationDialog = false
                    }
                ) {
                    Text(stringResource(R.string.remove))
                }
            },
        )
    }


}


data class BookWithAnnotations(
    val book: Book,
    val annotation: List<BookAnnotation>
)
