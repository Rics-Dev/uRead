package com.ricdev.uread.presentation.sharedComponents


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ricdev.uread.R
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.model.Shelf
import com.ricdev.uread.presentation.home.HomeViewModel
import com.ricdev.uread.presentation.sharedComponents.dialogs.AddShelfDialog
import com.ricdev.uread.util.PurchaseHelper

@Composable
fun Shelves(
    viewModel: HomeViewModel,
    appPreferences: AppPreferences,
    shelves: List<Shelf>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onAddShelf: (String) -> Unit,
    purchaseHelper: PurchaseHelper,
) {

//    var showPremiumModal by remember { mutableStateOf(false) }


    val context = LocalContext.current

    var showAddShelfDialog by remember { mutableStateOf(false) }
    var newShelfName by remember { mutableStateOf("") }

    ScrollableTabRow(
        selectedTabIndex = selectedTab,
        edgePadding = 16.dp
    ) {
        Tab(
            text = { Text(stringResource(R.string.all_books)) },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        )
        shelves.forEachIndexed { index, shelf ->
            Tab(
                text = { Text(shelf.name) },
                selected = selectedTab == index,
                onClick = { onTabSelected(index + 1) }
            )
        }
        Tab(
            icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = "New Shelf") },
            selected = false,
            onClick = {
                if (shelves.isNotEmpty() && !appPreferences.isPremium) {
                    viewModel.purchasePremium(purchaseHelper)
//                    showPremiumModal = true
                } else {
                    showAddShelfDialog = true
                }
            }
        )
    }

    if (showAddShelfDialog) {
        AddShelfDialog(
            newShelfName = newShelfName,
            onShelfNameChange = { newShelfName = it },
            shelves = listOf("All Books") + shelves.map { it.name },
            onAddShelf = onAddShelf,
            onDismiss = { showAddShelfDialog = false },
            context = context
        )
    }


//    if (showPremiumModal) {
//        PremiumModal(
//            purchaseHelper = purchaseHelper,
//            hidePremiumModal = { showPremiumModal = false }
//        )
//    }
}



