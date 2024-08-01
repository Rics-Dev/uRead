package com.example.uread.util

import android.graphics.Bitmap
import androidx.documentfile.provider.DocumentFile
import org.readium.r2.shared.publication.Publication


data class Book(
    val documentFile: DocumentFile,
    val title: String,
    val authors: List<String>,
    val description: String?,
    val coverBitmap: Bitmap?
)