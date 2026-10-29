package com.trodar.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.trodar.database.model.FolderOrNoteItem
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteadDao {
    // 3.1 Выбрать все папки и записи: папки первые
    @Query("""
        SELECT id, parent_id AS parentId, name AS title, 0 AS isFolder, created_at AS createdAt, updated_at AS updatedAt 
        FROM folder
        WHERE (:parentId IS NULL AND parent_id IS NULL) OR (parent_id = :parentId)
        UNION ALL
        SELECT id, folder_id AS parentId, title, 1 AS isFolder, created_at AS createdAt, updated_at AS updatedAt 
        FROM note
        WHERE (:parentId IS NULL AND folder_id IS NULL) OR (folder_id = :parentId)
        ORDER BY isFolder ASC, updatedAt DESC
    """)
    fun getFoldersAndNotes(parentId: Long? = null): Flow<List<FolderOrNoteItem>>

    @Query("""
        SELECT id, parent_id AS parentId, name AS title, 0 AS isFolder, created_at AS createdAt, updated_at AS updatedAt 
        FROM folder
        UNION ALL
        SELECT id, folder_id AS parentId, title, 1 AS isFolder, created_at AS createdAt, updated_at AS updatedAt 
        FROM note
        ORDER BY isFolder ASC, updatedAt DESC
    """)
    fun getAllFoldersAndNotes(): Flow<List<FolderOrNoteItem>>
}
