package com.trodar.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.trodar.database.entity.FolderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {
    @Query("SELECT * FROM folder ORDER BY name ASC")
    fun getAllFolders(): Flow<List<FolderEntity>>

    @Query("SELECT * FROM folder WHERE parent_id = :parentId ORDER BY name ASC")
    fun getFoldersByParentId(parentId: Long?): Flow<List<FolderEntity>>

    @Query("SELECT * FROM folder WHERE id = :id")
    suspend fun getFolderById(id: Long): FolderEntity?

    @Query("SELECT * FROM folder WHERE id = :id")
    fun observeFolderById(id: Long): Flow<FolderEntity?>

    @Upsert
    suspend fun upsertFolder(folder: FolderEntity): Long

    @Delete
    suspend fun deleteFolder(folder: FolderEntity)

    @Query("DELETE FROM folder WHERE id = :id")
    suspend fun deleteFolderById(id: Long)
}
