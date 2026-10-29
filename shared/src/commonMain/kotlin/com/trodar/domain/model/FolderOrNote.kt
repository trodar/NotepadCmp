package com.trodar.domain.model

sealed interface FolderOrNote {
    data class FolderItem(val folder: Folder) : FolderOrNote
    data class NoteItem(val note: Note) : FolderOrNote
}
