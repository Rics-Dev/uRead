package com.example.uread.domain.model

data class Book(
    val uri: String,
    val title: String,
    val authors: String,
    val description: String?,
    val coverPath: String?,
    val lastModified: Long
)
