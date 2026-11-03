package com.trodar.data.repository

import androidx.datastore.core.DataStore
import com.trodar.data.datastore.UserPreferences
import com.trodar.data.model.BackupDatabaseData
import com.trodar.data.model.toBackupDto
import com.trodar.data.model.toEntity
import com.trodar.database.NoteDatabase
import com.trodar.domain.repository.BackupRepository
import com.trodar.util.ZipEntryData
import com.trodar.util.unzipEntries
import com.trodar.util.zipEntries
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf

@OptIn(ExperimentalSerializationApi::class)
class BackupRepositoryImpl(
    private val database: NoteDatabase,
    private val dataStore: DataStore<UserPreferences>,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BackupRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    override suspend fun exportBackup(): ByteArray = withContext(ioDispatcher) {
        val folders = database.folderDao().getAllFoldersList().map { it.toBackupDto() }
        val notes = database.noteDao().getAllNotesList().map { it.toBackupDto() }
        val noteTexts = database.noteDao().getAllNoteTextsList().map { it.toBackupDto() }

        val dbData = BackupDatabaseData(
            version = 1,
            folders = folders,
            notes = notes,
            noteTexts = noteTexts
        )

        val dbJson = json.encodeToString(BackupDatabaseData.serializer(), dbData)

        val entries = mutableListOf<ZipEntryData>()
        entries.add(ZipEntryData(DB_DATA_FILE, dbJson.encodeToByteArray()))

        val prefs = dataStore.data.first()
        val prefsBytes = ProtoBuf.encodeToByteArray(UserPreferences.serializer(), prefs)
        entries.add(ZipEntryData(USER_PREFERENCES_FILE, prefsBytes))

        zipEntries(entries)
    }

    override suspend fun importBackup(bytes: ByteArray): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val entries = unzipEntries(bytes)
            if (entries.isEmpty()) {
                error("Backup file is empty or invalid")
            }

            val dbDataEntry = entries.firstOrNull { it.name == DB_DATA_FILE }
            if (dbDataEntry != null) {
                val jsonString = dbDataEntry.bytes.decodeToString()
                val backupData = json.decodeFromString(BackupDatabaseData.serializer(), jsonString)

                if (backupData.folders.isNotEmpty()) {
                    val sortedFolders = backupData.folders.sortedWith { a, b ->
                        if (a.parentId == null && b.parentId != null) -1
                        else if (a.parentId != null && b.parentId == null) 1
                        else a.id.compareTo(b.id)
                    }
                    database.folderDao().upsertFolders(sortedFolders.map { it.toEntity() })
                }
                if (backupData.notes.isNotEmpty()) {
                    database.noteDao().upsertNotes(backupData.notes.map { it.toEntity() })
                }
                if (backupData.noteTexts.isNotEmpty()) {
                    database.noteDao().upsertNoteTexts(backupData.noteTexts.map { it.toEntity() })
                }
            }

            val prefsEntry = entries.firstOrNull { it.name == USER_PREFERENCES_FILE }
            if (prefsEntry != null) {
                val importedPrefs = ProtoBuf.decodeFromByteArray(UserPreferences.serializer(), prefsEntry.bytes)
                dataStore.updateData { importedPrefs }
            }
        }
    }

    private companion object {
        const val DB_DATA_FILE = "db_data.json"
        const val USER_PREFERENCES_FILE = "user_preferences.pb"
    }
}
