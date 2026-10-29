package com.trodar.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.time.Clock

@Entity(
    tableName = "note",
    foreignKeys = [ForeignKey(
        entity = FolderEntity::class,
        parentColumns = ["id"],
        childColumns = ["folder_id"],
        onDelete = ForeignKey.CASCADE      // или SET_NULL, чтобы записи уходили в корень
    )],
    indices = [Index(value = ["folder_id", "updated_at"])]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "folder_id") val folderId: Long? = null,   // null = без папки
    val title: String = "",
    @ColumnInfo(name = "bg_color") val bgColor: Int = 0xFFFFFFFF.toInt(),
    @ColumnInfo(name = "text_color") val textColor: Int = 0xFF000000.toInt(),
    @ColumnInfo(name = "frame_id") val frameId: Int = 0,
    @ColumnInfo(name = "created_at") val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = Clock.System.now().toEpochMilliseconds()
)
