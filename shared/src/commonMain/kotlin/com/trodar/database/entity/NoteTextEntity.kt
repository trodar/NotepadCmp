package com.trodar.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "note_text",
    foreignKeys = [ForeignKey(
        entity = NoteEntity::class,
        parentColumns = ["id"],
        childColumns = ["note_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class NoteTextEntity(
    @PrimaryKey @ColumnInfo(name = "note_id") val noteId: Long,
    val content: String = ""
)
