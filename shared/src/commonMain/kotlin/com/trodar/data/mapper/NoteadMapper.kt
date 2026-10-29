package com.trodar.data.mapper

import com.trodar.database.entity.FolderEntity
import com.trodar.database.entity.NoteEntity
import com.trodar.database.model.FolderOrNoteItem
import com.trodar.domain.model.Folder
import com.trodar.domain.model.FolderOrNote
import com.trodar.domain.model.Note

fun FolderEntity.toDomain(): Folder = Folder(
    id = id,
    parentId = parentId,
    name = name,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Folder.toEntity(): FolderEntity = FolderEntity(
    id = id,
    parentId = parentId,
    name = name,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun NoteEntity.toDomain(): Note = Note(
    id = id,
    folderId = folderId,
    title = title,
    bgColor = bgColor,
    textColor = textColor,
    frameId = frameId,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id,
    folderId = folderId,
    title = title,
    bgColor = bgColor,
    textColor = textColor,
    frameId = frameId,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun FolderOrNoteItem.toDomain(): FolderOrNote = if (isFolder) {
    FolderOrNote.FolderItem(
        Folder(
            id = id,
            parentId = parentId,
            name = title,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    )
} else {
    FolderOrNote.NoteItem(
        Note(
            id = id,
            folderId = parentId,
            title = title,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    )
}
