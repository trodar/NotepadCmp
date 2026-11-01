package com.trodar.data.repository

import com.trodar.data.mapper.toDomain
import com.trodar.data.mapper.toEntity
import com.trodar.database.dao.FolderDao
import com.trodar.database.dao.NoteDao
import com.trodar.database.dao.NoteadDao
import com.trodar.domain.model.Folder
import com.trodar.domain.model.FolderOrNote
import com.trodar.domain.model.Note
import com.trodar.domain.model.NoteWithText
import com.trodar.domain.repository.NoteadRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

import kotlin.time.Clock

class NoteadRepositoryImpl(
    private val folderDao: FolderDao,
    private val noteDao: NoteDao,
    private val noteadDao: NoteadDao
) : NoteadRepository {

    override fun getFoldersAndNotes(parentId: Long?): Flow<List<FolderOrNote>> {
        return noteadDao.getFoldersAndNotes(parentId)
            .map { list -> list.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getAllFoldersAndNotes(): Flow<List<FolderOrNote>> {
        return noteadDao.getAllFoldersAndNotes()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override fun observeFolder(id: Long): Flow<Folder?> {
        return folderDao.observeFolderById(id)
            .map { it?.toDomain() }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getFolderById(id: Long): Folder? = withContext(Dispatchers.IO) {
        folderDao.getFolderById(id)?.toDomain()
    }

    override suspend fun getNoteWithText(noteId: Long): NoteWithText? = withContext(Dispatchers.IO) {
        val noteEntity = noteDao.getNoteById(noteId) ?: return@withContext null
        val textEntity = noteDao.getNoteText(noteId)
        NoteWithText(
            note = noteEntity.toDomain(),
            content = textEntity?.content ?: ""
        )
    }

    override suspend fun upsertFolder(folder: Folder): Long = withContext(Dispatchers.IO) {
        folderDao.upsertFolder(folder.toEntity())
    }

    override suspend fun deleteFolder(folderId: Long) = withContext(Dispatchers.IO) {
        folderDao.deleteFolderById(folderId)
    }

    override suspend fun saveNoteWithContent(note: Note, content: String): Long = withContext(Dispatchers.IO) {
        val plainText = extractPlainText(content)
        val title = plainText.take(20)
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val updatedNote = note.copy(
            title = title,
            createdAt = if (note.id == 0L) currentTime else note.createdAt,
            updatedAt = currentTime
        )
        noteDao.saveNoteWithText(updatedNote.toEntity(), content)
    }

    private fun extractPlainText(html: String): String {
        return html
            .replace(Regex("<[^>]*>"), "")
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    override suspend fun deleteNote(noteId: Long) = withContext(Dispatchers.IO) {
        noteDao.deleteNoteById(noteId)
    }
}
