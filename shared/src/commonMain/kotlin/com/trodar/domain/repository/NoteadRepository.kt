package com.trodar.domain.repository

import com.trodar.domain.model.Folder
import com.trodar.domain.model.FolderOrNote
import com.trodar.domain.model.Note
import com.trodar.domain.model.NoteWithText
import kotlinx.coroutines.flow.Flow

interface NoteadRepository {
    fun getFoldersAndNotes(parentId: Long?): Flow<List<FolderOrNote>>
    fun getAllFoldersAndNotes(): Flow<List<FolderOrNote>>
    suspend fun getFolderById(id: Long): Folder?
    suspend fun getNoteWithText(noteId: Long): NoteWithText?
    suspend fun upsertFolder(folder: Folder): Long
    suspend fun deleteFolder(folderId: Long)
    suspend fun saveNoteWithContent(note: Note, content: String): Long
    suspend fun deleteNote(noteId: Long)
}
