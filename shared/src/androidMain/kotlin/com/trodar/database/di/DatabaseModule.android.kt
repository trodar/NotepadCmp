package com.trodar.database.di

import com.trodar.database.getDatabaseBuilder
import com.trodar.util.AndroidCustomBackgroundStorage
import com.trodar.util.AndroidFileExporter
import com.trodar.util.CustomBackgroundStorage
import com.trodar.util.FileExporter
import org.koin.dsl.module

actual val platformDatabaseModule = module {
    single { getDatabaseBuilder(context = get()) }
    single<FileExporter> { AndroidFileExporter(context = get()) }
    single<CustomBackgroundStorage> { AndroidCustomBackgroundStorage(context = get()) }
}
