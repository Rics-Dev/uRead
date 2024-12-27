package com.ricdev.uread.presentation.shelves

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.ricdev.uread.R
import com.ricdev.uread.data.model.Shelf
import com.ricdev.uread.navigation.Screens
import com.ricdev.uread.presentation.sharedComponents.CustomNavigationDrawer
//import com.ricsdev.uread.presentation.sharedComponents.PremiumModal
import com.ricdev.uread.presentation.sharedComponents.dialogs.AddShelfDialog
import com.ricdev.uread.presentation.sharedComponents.dialogs.DeleteShelfDialog
import com.ricdev.uread.util.PurchaseHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelvesScreen(
    navController: NavHostController,
    purchaseHelper: PurchaseHelper,
    viewModel: ShelvesViewModel = hiltViewModel()
) {
    val context = LocalContext.current


    val shelvesState by viewModel.shelvesState.collectAsStateWithLifecycle()
    var selectedShelf by remember { mutableStateOf<Shelf?>(null) }
    var showAddShelfDialog by remember { mutableStateOf(false) }
    var showEditShelfDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var newShelfName by remember { mutableStateOf("") }


    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val appPreferences by viewModel.appPreferences.collectAsStateWithLifecycle()

//    var showPremiumModal by remember { mutableStateOf(false) }


    CustomNavigationDrawer(
        purchaseHelper = purchaseHelper,
        drawerState = drawerState,
        navController = navController
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.shelves)) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (shelvesState.let { it is ShelvesState.Success && it.shelves.isNotEmpty() } && !appPreferences.isPremium) {
                            navController.navigate(Screens.PremiumScreen.route);
//                            showPremiumModal = true
//                            viewModel.purchasePremium(purchaseHelper)
                        } else {
                            showAddShelfDialog = true
                        }
                    },
                    content = {
                        Row(
                            modifier = Modifier
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Shelf")
                            Text(text = stringResource(R.string.add_shelf))
                        }
                    }
                )
            }
        ) { paddingValues ->
            when (val state = shelvesState) {
                is ShelvesState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        itemsIndexed(state.shelves) { index, shelf ->
                            ListItem(
                                colors = ListItemDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .shadow(
                                        4.dp,
                                        shape = RoundedCornerShape(16.dp),
                                        clip = true
                                    )
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable(onClick = {
                                        showEditShelfDialog = true
                                        selectedShelf = shelf
                                    }),
                                headlineContent = {
                                    Text(
                                        shelf.name,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                trailingContent = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        IconButton(
                                            onClick = {
                                                viewModel.moveShelf(index, index - 1)
                                            },
                                            enabled = index > 0
                                        ) {
                                            Icon(
                                                Icons.Default.KeyboardArrowUp,
                                                contentDescription = "Move Up"
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                viewModel.moveShelf(index, index + 1)
                                            },
                                            enabled = index < state.shelves.size - 1
                                        ) {
                                            Icon(
                                                Icons.Default.KeyboardArrowDown,
                                                contentDescription = "Move Down"
                                            )
                                        }
                                        IconButton(onClick = {
                                            selectedShelf = shelf
                                            showDeleteDialog = true
                                        }) {
                                            Icon(
                                                Icons.Outlined.Delete,
                                                contentDescription = "Delete",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

                is ShelvesState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is ShelvesState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: ${state.message}")
                    }
                }
            }


            if (showAddShelfDialog) {
                AddShelfDialog(
                    newShelfName = newShelfName,
                    onShelfNameChange = { newShelfName = it },
                    shelves = ((shelvesState as? ShelvesState.Success)?.shelves?.map { it.name }
                        ?: emptyList()) + listOf("All Books"),
                    onAddShelf = { viewModel.addShelf(it) },
                    onDismiss = { showAddShelfDialog = false },
                    context = context
                )
            }


            if (showEditShelfDialog && selectedShelf != null) {
                var editedShelfName by remember { mutableStateOf(selectedShelf!!.name) }

                AlertDialog(
                    onDismissRequest = {
                        showEditShelfDialog = false
                        selectedShelf = null
                    },
                    title = { Text(stringResource(R.string.edit_shelf)) },
                    text = {
                        OutlinedTextField(
                            value = editedShelfName,
                            onValueChange = { editedShelfName = it },
                            label = { Text(stringResource(R.string.shelf_name)) }
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            when {
                                editedShelfName.isBlank() -> {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.shelf_name_is_required),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                (shelvesState as? ShelvesState.Success)?.shelves?.any {
                                    it.name.equals(
                                        editedShelfName,
                                        ignoreCase = true
                                    ) && it.id != selectedShelf!!.id
                                } == true -> {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.shelf_name_already_exists),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> {
                                    val updatedShelf = selectedShelf!!.copy(name = editedShelfName)
                                    viewModel.updateShelf(updatedShelf)
                                    selectedShelf = null
                                    showEditShelfDialog = false
                                }
                            }
                        }) {
                            Text(stringResource(R.string.save))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditShelfDialog = false }) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                )
            }



            if (showDeleteDialog && selectedShelf != null) {
                DeleteShelfDialog(
                    selectedShelf = selectedShelf,
                    onDismiss = {
                        showDeleteDialog = false
                        selectedShelf = null
                    },
                    onConfirmDelete = { viewModel.deleteShelf(it) }
                )
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



