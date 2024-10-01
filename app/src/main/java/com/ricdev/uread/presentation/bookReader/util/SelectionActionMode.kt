package com.ricdev.uread.presentation.bookReader.util

import android.graphics.Rect
import android.graphics.RectF
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



class SelectionActionModeCallback(
    private val showCustomMenu: (Rect, String) -> Unit,
    private val hideCustomMenu: () -> Unit,
    private val getSelectedText: suspend () -> String?,
    private val getSelectionPosition: suspend () -> RectF?
) : ActionMode.Callback {
    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        CoroutineScope(Dispatchers.Main).launch {
            val selectionRectF = getSelectionPosition()
            val selectedText = getSelectedText()
            val selectionRect = selectionRectF?.let {
                Rect(it.left.toInt(), it.top.toInt(), it.right.toInt(), it.bottom.toInt())
            }
            if (selectedText != null && selectionRect != null) {
                showCustomMenu(selectionRect, selectedText)
            }
        }
        return true
    }


    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean = false

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean = false

    override fun onDestroyActionMode(mode: ActionMode) {
        hideCustomMenu()
    }
}







