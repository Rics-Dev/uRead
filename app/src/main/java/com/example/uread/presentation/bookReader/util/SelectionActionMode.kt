package com.example.uread.presentation.bookReader.util

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import com.example.uread.R

class SelectionActionModeCallback(
    private val onHighlight: () -> Unit,
    private val onUnderline: () -> Unit,
) : ActionMode.Callback {
    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        mode.menuInflater.inflate(R.menu.text_selection_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean = false

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_highlight -> {
                onHighlight()
                mode.finish()
                return true
            }
            R.id.action_underline -> {
                onUnderline()
                mode.finish()
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