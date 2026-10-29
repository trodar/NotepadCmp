package com.trodar.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.trodar.database.dao.FolderDao
import com.trodar.database.dao.NoteDao
import com.trodar.database.dao.NoteadDao
import com.trodar.database.entity.FolderEntity
import com.trodar.database.entity.NoteEntity
import com.trodar.database.entity.NoteTextEntity

@Database(
    entities = [FolderEntity::class, NoteEntity::class, NoteTextEntity::class],
    version = 1,
    exportSchema = true
)
@ConstructedBy(NoteDatabaseConstructor::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun folderDao(): FolderDao
    abstract fun noteDao(): NoteDao
    abstract fun noteadDao(): NoteadDao
}

expect object NoteDatabaseConstructor : RoomDatabaseConstructor<NoteDatabase> {
    override fun initialize(): NoteDatabase
}
