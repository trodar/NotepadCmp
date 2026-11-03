package com.trodar.data.model

import com.trodar.database.entity.FolderEntity
import com.trodar.database.entity.NoteEntity
import com.trodar.database.entity.NoteTextEntity
import kotlinx.serialization.Serializable

@Serializable
data class FolderBackupDto(
    val id: Long,
    val parentId: Long? = null,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long
)

fun FolderEntity.toBackupDto(): FolderBackupDto = FolderBackupDto(
    id = id,
    parentId = parentId,
    name = name,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun FolderBackupDto.toEntity(): FolderEntity = FolderEntity(
    id = id,
    parentId = parentId,
    name = name,
    createdAt = createdAt,
    updatedAt = updatedAt
)

@Serializable
data class NoteBackupDto(
    val id: Long,
    val folderId: Long? = null,
    val title: String = "",
    val bgColor: Int,
    val textColor: Int,
    val frameId: Int = 0,
    val createdAt: Long,
    val updatedAt: Long
)

fun NoteEntity.toBackupDto(): NoteBackupDto = NoteBackupDto(
    id = id,
    folderId = folderId,
    title = title,
    bgColor = bgColor,
    textColor = textColor,
    frameId = frameId,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun NoteBackupDto.toEntity(): NoteEntity = NoteEntity(
    id = id,
    folderId = folderId,
    title = title,
    bgColor = bgColor,
    textColor = textColor,
    frameId = frameId,
    createdAt = createdAt,
    updatedAt = updatedAt
)

@Serializable
data class NoteTextBackupDto(
    val noteId: Long,
    val content: String = ""
)

fun NoteTextEntity.toBackupDto(): NoteTextBackupDto = NoteTextBackupDto(
    noteId = noteId,
    content = content
)

fun NoteTextBackupDto.toEntity(): NoteTextEntity = NoteTextEntity(
    noteId = noteId,
    content = content
)

@Serializable
data class BackupDatabaseData(
    val version: Int = 1,
    val folders: List<FolderBackupDto> = emptyList(),
    val notes: List<NoteBackupDto> = emptyList(),
    val noteTexts: List<NoteTextBackupDto> = emptyList()
)
