package com.trodar.database.di

import com.trodar.database.getDatabaseBuilder
import org.koin.dsl.module

actual val platformDatabaseModule = module {
    single { getDatabaseBuilder(context = get()) }
}
