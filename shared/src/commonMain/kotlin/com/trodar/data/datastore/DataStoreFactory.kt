package com.trodar.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM

fun createDataStore(producePath: () -> String): DataStore<UserPreferences> {
    return DataStoreFactory.create(
        storage = OkioStorage(
            fileSystem = FileSystem.SYSTEM,
            serializer = UserPreferencesSerializer,
            producePath = { producePath().toPath() }
        )
    )
}
