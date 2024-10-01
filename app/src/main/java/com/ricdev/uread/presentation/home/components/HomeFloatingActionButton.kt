package com.ricdev.uread.presentation.home.components

//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.expandVertically
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.fadeOut
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.layout.wrapContentSize
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.material3.FilledTonalButton
//import androidx.compose.material3.FloatingActionButton
//import androidx.compose.material3.Icon
//import androidx.compose.material3.SmallFloatingActionButton
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp

//@Composable
//fun HomeFloatingActionButton() {
//
//    var isExpanded by remember { mutableStateOf(false) }
//
//
//
//
//    Column(
//        modifier = Modifier.wrapContentSize(),
//        horizontalAlignment = Alignment.End,
//    ) {
//        AnimatedVisibility(
//            visible = isExpanded,
//            enter = fadeIn() + expandVertically(),
//            exit = fadeOut(),
//        ) {
//            Column(
//                modifier = Modifier.padding(4.dp),
//                horizontalAlignment = Alignment.End,
//            ) {
//                Row {
//                    FilledTonalButton(
//                        contentPadding = PaddingValues(8.dp),
//                        shape = RoundedCornerShape(12.dp),
//                        onClick = { /*TODO*/ }
//                    ) {
//                        Text("Search in Open Library")
//                    }
//                    Spacer(modifier = Modifier.width(16.dp))
//                    SmallFloatingActionButton(
//                        onClick = { /* Handle click */ },
//                    ) {
//                        Icon(Icons.Filled.Search, contentDescription = "Search")
//                    }
//                }
//                Spacer(modifier = Modifier.height(5.dp))
//                Row {
//                    FilledTonalButton(
//                        contentPadding = PaddingValues(8.dp),
//                        shape = RoundedCornerShape(12.dp),
//                        onClick = { /*TODO*/ }
//                    ) {
//                        Text("Add Manually")
//                    }
//                    Spacer(modifier = Modifier.width(16.dp))
//                    SmallFloatingActionButton(
//                        onClick = { /* Handle click */ },
//                    ) {
//                        Icon(Icons.Filled.Add, contentDescription = "Add")
//                    }
//                }
//            }
//        }
//        Spacer(modifier = Modifier.height(12.dp))
//        FloatingActionButton(
//            onClick = { isExpanded = !isExpanded }
//        ) {
//            Icon(
//                if (isExpanded) Icons.Filled.Close else Icons.Filled.Add,
//                contentDescription = if (isExpanded) "Close" else "Add"
//            )
//        }
//    }
//
//}