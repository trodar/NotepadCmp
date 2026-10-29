package com.trodar.notead.di

import com.trodar.data.repository.NoteadRepositoryImpl
import com.trodar.database.di.platformDatabaseModule
import com.trodar.database.di.sharedDatabaseModule
import com.trodar.domain.repository.NoteadRepository
import com.trodar.presentation.editor.NoteEditorViewModel
import com.trodar.presentation.list.NoteadListViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    singleOf(::NoteadRepositoryImpl) bind NoteadRepository::class
    viewModelOf(::NoteadListViewModel)
    viewModel { parameters ->
        NoteEditorViewModel(
            repository = get(),
            noteId = parameters.get(),
            folderId = parameters.getOrNull()
        )
    }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}): KoinApplication {
    return startKoin {
        appDeclaration()
        modules(
            platformDatabaseModule,
            sharedDatabaseModule,
            platformDataStoreModule,
            settingsModule,
            appModule
        )
    }
}
