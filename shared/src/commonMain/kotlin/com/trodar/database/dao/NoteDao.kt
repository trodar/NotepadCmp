package com.trodar.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.trodar.database.entity.NoteEntity
import com.trodar.database.entity.NoteTextEntity
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock

@Dao
interface NoteDao {
    @Query("SELECT * FROM note ORDER BY updated_at DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM note WHERE folder_id = :folderId ORDER BY updated_at DESC")
    fun getNotesByFolderId(folderId: Long?): Flow<List<NoteEntity>>

    @Query("SELECT * FROM note WHERE id = :id")
    suspend fun getNoteById(id: Long): NoteEntity?

    @Query("SELECT * FROM note_text WHERE note_id = :noteId")
    suspend fun getNoteText(noteId: Long): NoteTextEntity?

    @Upsert
    suspend fun upsertNote(note: NoteEntity): Long

    @Upsert
    suspend fun upsertNoteText(noteText: NoteTextEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM note WHERE id = :id")
    suspend fun deleteNoteById(id: Long)

    @Transaction
    suspend fun saveNoteWithText(note: NoteEntity, content: String): Long {
        val noteId = upsertNote(note)
        val actualNoteId = if (note.id == 0L) noteId else note.id
        upsertNoteText(NoteTextEntity(noteId = actualNoteId, content = content))
        return actualNoteId
    }
}
