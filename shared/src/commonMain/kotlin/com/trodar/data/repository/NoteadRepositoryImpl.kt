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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteadRepositoryImpl(
    private val folderDao: FolderDao,
    private val noteDao: NoteDao,
    private val noteadDao: NoteadDao
) : NoteadRepository {

    override fun getFoldersAndNotes(parentId: Long?): Flow<List<FolderOrNote>> {
        return noteadDao.getFoldersAndNotes(parentId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getAllFoldersAndNotes(): Flow<List<FolderOrNote>> {
        return noteadDao.getAllFoldersAndNotes().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getFolderById(id: Long): Folder? {
        return folderDao.getFolderById(id)?.toDomain()
    }

    override suspend fun getNoteWithText(noteId: Long): NoteWithText? {
        val noteEntity = noteDao.getNoteById(noteId) ?: return null
        val textEntity = noteDao.getNoteText(noteId)
        return NoteWithText(
            note = noteEntity.toDomain(),
            content = textEntity?.content ?: ""
        )
    }

    override suspend fun upsertFolder(folder: Folder): Long {
        return folderDao.upsertFolder(folder.toEntity())
    }

    override suspend fun deleteFolder(folderId: Long) {
        folderDao.deleteFolderById(folderId)
    }

    override suspend fun saveNoteWithContent(note: Note, content: String): Long {
        return noteDao.saveNoteWithContent(note.toEntity(), content)
    }

    override suspend fun deleteNote(noteId: Long) {
        noteDao.deleteNoteById(noteId)
    }
}
