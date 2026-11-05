package com.trodar.database.di

import com.trodar.database.getDatabaseBuilder
import com.trodar.util.CustomBackgroundStorage
import com.trodar.util.FileExporter
import com.trodar.util.IosCustomBackgroundStorage
import com.trodar.util.IosFileExporter
import org.koin.dsl.module

actual val platformDatabaseModule = module {
    single { getDatabaseBuilder() }
    single<FileExporter> { IosFileExporter() }
    single<CustomBackgroundStorage> { IosCustomBackgroundStorage() }
}
