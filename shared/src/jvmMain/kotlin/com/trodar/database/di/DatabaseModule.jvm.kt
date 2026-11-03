package com.trodar.database.di

import com.trodar.database.getDatabaseBuilder
import com.trodar.util.FileExporter
import com.trodar.util.JvmFileExporter
import org.koin.dsl.module

actual val platformDatabaseModule = module {
    single { getDatabaseBuilder() }
    single<FileExporter> { JvmFileExporter() }
}
