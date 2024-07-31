package com.example.uread.util

import androidx.documentfile.provider.DocumentFile
import org.readium.r2.shared.publication.Publication

data class Book(
    val publication: Publication,
    val documentFile: DocumentFile
)
