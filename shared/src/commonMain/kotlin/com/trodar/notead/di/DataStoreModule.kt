package com.trodar.notead.di

import com.trodar.data.repository.SettingsRepositoryImpl
import com.trodar.domain.repository.SettingsRepository
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformDataStoreModule: Module

val settingsModule = module {
    single { SettingsRepositoryImpl(get()) } bind SettingsRepository::class
}
