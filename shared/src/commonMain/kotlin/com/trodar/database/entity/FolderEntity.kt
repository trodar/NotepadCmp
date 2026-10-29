package com.trodar.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.time.Clock

@Entity(
    tableName = "folder",
    foreignKeys = [ForeignKey(
        entity = FolderEntity::class,
        parentColumns = ["id"],
        childColumns = ["parent_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("parent_id")]
)
data class FolderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "parent_id") val parentId: Long? = null,   // null = корень
    val name: String,
    @ColumnInfo(name = "created_at") val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = Clock.System.now().toEpochMilliseconds()
)
