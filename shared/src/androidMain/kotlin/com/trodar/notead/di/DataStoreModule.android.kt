package com.trodar.notead.di

import android.content.Context
import com.trodar.data.datastore.createDataStore
import org.koin.dsl.module

actual val platformDataStoreModule = module {
    single {
        createDataStore(
            producePath = {
                val context: Context = get()
                context.filesDir.resolve("user_preferences.preferences_pb").absolutePath
            }
        )
    }
}
