package com.trodar.notead.di

import com.trodar.data.datastore.createDataStore
import org.koin.dsl.module

actual val platformDataStoreModule = module {
    single {
        createDataStore(
            producePath = { "user_preferences.preferences_pb" }
        )
    }
}
