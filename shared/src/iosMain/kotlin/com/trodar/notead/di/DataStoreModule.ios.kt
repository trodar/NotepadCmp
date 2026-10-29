package com.trodar.notead.di

import com.trodar.data.datastore.createDataStore
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import org.koin.dsl.module

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual val platformDataStoreModule = module {
    single {
        createDataStore(
            producePath = {
                val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = false,
                    error = null
                )
                requireNotNull(documentDirectory?.path) + "/user_preferences.preferences_pb"
            }
        )
    }
}
