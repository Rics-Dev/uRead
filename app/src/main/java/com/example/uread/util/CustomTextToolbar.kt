package com.example.uread.util
//
//import androidx.compose.foundation.text.selection.LocalTextSelectionColors
//import androidx.compose.foundation.text.selection.SelectionContainer
//import androidx.compose.material3.Button
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.platform.LocalTextToolbar
//import androidx.compose.ui.platform.TextToolbar
//import androidx.compose.ui.platform.TextToolbarStatus
//import androidx.compose.ui.text.input.TextFieldValue
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import org.readium.r2.navigator.SelectableNavigator
//import org.readium.r2.navigator.epub.EpubNavigatorFragment
//
//@Composable
//fun CustomTextSelectionToolbar(
//    selectedText: String?,
//    onHighlight: () -> Unit,
//    onNote: () -> Unit,
//    dismiss: () -> Unit
//) {
//    val textToolbar = LocalTextToolbar.current
//    val toolbarStatus = textToolbar.status
//
//    if (toolbarStatus == TextToolbarStatus.Shown && selectedText != null) {
//        // Show your custom toolbar
//        TextToolbar {
//            Button(onClick = {
//                onHighlight()
//                dismiss()
//            }) {
//                Text("Highlight")
//            }
//            Button(onClick = {
//                onNote()
//                dismiss()
//            }) {
//                Text("Note")
//            }
//        }
//    }
//}
//
//@Composable
//fun rememberCustomTextSelection(
//    onHighlight: () -> Unit,
//    onNote: () -> Unit
//): TextToolbar {
//    val textToolbar = LocalTextToolbar.current
//    val showCustomToolbar = remember { mutableStateOf(false) }
//
//    return TextToolbar(
//        onCopy = {
//            showCustomToolbar.value = true
//        },
//        onCut = {},
//        onPaste = {},
//        onSelectAll = {},
//        onCustomToolbar = {
//            if (showCustomToolbar.value) {
//                CustomTextSelectionToolbar(
//                    selectedText = it,
//                    onHighlight = onHighlight,
//                    onNote = onNote,
//                    dismiss = { showCustomToolbar.value = false }
//                )
//            }
//        }
//    )
//}
