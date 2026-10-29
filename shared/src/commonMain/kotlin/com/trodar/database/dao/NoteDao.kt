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

    // 4. NoteEntity.title берется первые 50 символов из текста. обновляется при сохранении текста.
    @Transaction
    suspend fun saveNoteWithContent(note: NoteEntity, content: String): Long {
        val plainText = content.replace(Regex("<[^>]*>"), "").trim()
        val title = plainText.take(50)
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val updatedNote = note.let {
            if (it.id == 0L) {
                it.copy(title = title, createdAt = currentTime, updatedAt = currentTime)
            } else {
                it.copy(title = title, updatedAt = currentTime)
            }
        }

        val noteId = upsertNote(updatedNote)
        val actualNoteId = if (note.id == 0L) noteId else note.id

        upsertNoteText(NoteTextEntity(noteId = actualNoteId, content = content))
        return actualNoteId
    }
}
