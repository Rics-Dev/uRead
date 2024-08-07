package com.example.uread.presentation.bookReader.util

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.compose.ui.graphics.Color
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
                showActionMode(true) { color ->
                    onHighlight(color)
                    mode.finish()
                }
                return true
            }
            R.id.action_underline -> {
                showActionMode(true) { color ->
                    onUnderline(color)
                    mode.finish()
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