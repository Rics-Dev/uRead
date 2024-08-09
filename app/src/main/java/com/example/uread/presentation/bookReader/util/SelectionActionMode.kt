package com.example.uread.presentation.bookReader.util

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.compose.ui.graphics.Color
import androidx.core.view.forEach
import com.example.uread.R

class SelectionActionModeCallback(
    private val onHighlight: (Color) -> Unit,
    private val onUnderline: (Color) -> Unit,
    private val showActionMode: (Boolean, (Color) -> Unit) -> Unit
) : ActionMode.Callback {
    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        mode.menuInflater.inflate(R.menu.text_selection_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean = false

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_highlight -> {
                mode.finish() // Close the current action mode
                showActionMode(true) { color ->
                    onHighlight(color)
                }
                return true
            }
            R.id.action_underline -> {
                mode.finish() // Close the current action mode
                showActionMode(true) { color ->
                    onUnderline(color)
                }
                return true
            }
        }
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        // Clear selection when action mode is destroyed
        // This will be handled by the navigator
    }
}


//class SelectionActionModeCallback(
//    private val onHighlight: (Color) -> Unit,
//    private val onUnderline: (Color) -> Unit,
//    private val showActionMode: (Boolean, (Color) -> Unit) -> Unit
//) : ActionMode.Callback {
//    private var isColorSelectionMode = false
//    private var currentAction: String? = null
//
//    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
//        mode.menuInflater.inflate(R.menu.text_selection_menu, menu)
//        return true
//    }
//
//    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
//        if (isColorSelectionMode) {
//            menu.clear()
//            mode.menuInflater.inflate(R.menu.color_selection_menu, menu)
//            menu.forEach { item ->
//                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
//                item.title = null  // This ensures the title is not displayed
//            }
//            mode.title = "Select Color"
//            return true
//        }
//        return false
//    }
//
//    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
//        when {
//            isColorSelectionMode -> {
//                val color = when (item.itemId) {
//                    R.id.color_red -> Color.Red
//                    R.id.color_blue -> Color.Blue
//                    R.id.color_green -> Color.Green
//                    // Add more colors as needed
//                    else -> return false
//                }
//                when (currentAction) {
//                    "highlight" -> onHighlight(color)
//                    "underline" -> onUnderline(color)
//                }
//                mode.finish()
//                return true
//            }
//            else -> {
//                when (item.itemId) {
//                    R.id.action_highlight -> {
//                        isColorSelectionMode = true
//                        currentAction = "highlight"
//                        mode.invalidate() // This will trigger onPrepareActionMode
//                        return true
//                    }
//                    R.id.action_underline -> {
//                        isColorSelectionMode = true
//                        currentAction = "underline"
//                        mode.invalidate() // This will trigger onPrepareActionMode
//                        return true
//                    }
//                }
//            }
//        }
//        return false
//    }
//
//    override fun onDestroyActionMode(mode: ActionMode) {
//        isColorSelectionMode = false
//        currentAction = null
//        // Clear selection when action mode is destroyed
//        // This will be handled by the navigator
//    }
//}