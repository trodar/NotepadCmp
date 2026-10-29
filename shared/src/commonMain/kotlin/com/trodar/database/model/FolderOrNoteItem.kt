package com.trodar.database.model

data class FolderOrNoteItem(
    val id: Long,
    val parentId: Long?,
    val title: String,
    val isFolder: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
