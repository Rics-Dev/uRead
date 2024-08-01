package com.example.uread.presentation.sharedComponents

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Shelves(
    shelves: MutableList<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {

    ScrollableTabRow(
        selectedTabIndex = selectedTab,
        edgePadding = 16.dp
    ) {
        shelves.forEachIndexed { index, title ->
            Tab(
                text = { Text(title) },
                selected = selectedTab == index,
                onClick = { onTabSelected(index) }
            )
        }
        Tab(
            icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = "New Shelf") },
            selected = false,
            onClick = {
                shelves.add("Shelf ${shelves.size} ")
                onTabSelected(shelves.size - 1)
            }
        )
    }







    

    
}