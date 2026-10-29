package com.trodar.database

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

fun getDatabaseBuilder(): RoomDatabase.Builder<NoteDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "notead.db")
    return Room.databaseBuilder<NoteDatabase>(
        name = dbFile.absolutePath
    )
}
