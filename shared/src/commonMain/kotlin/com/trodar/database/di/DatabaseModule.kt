package com.trodar.database.di

import androidx.room.RoomDatabase
import com.trodar.database.NoteDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformDatabaseModule: Module

val sharedDatabaseModule = module {
    single {
        get<RoomDatabase.Builder<NoteDatabase>>().build()
    }
    single { get<NoteDatabase>().folderDao() }
    single { get<NoteDatabase>().noteDao() }
    single { get<NoteDatabase>().noteadDao() }
}
