package com.trodar.presentation.list

import com.trodar.domain.model.AppTheme
import com.trodar.domain.model.Folder
import com.trodar.domain.model.FolderOrNote
import com.trodar.domain.model.Note
import com.trodar.domain.model.NoteWithText
import com.trodar.domain.repository.NoteadRepository
import com.trodar.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FakeSettingsRepository : SettingsRepository {
    private val _theme = MutableStateFlow(AppTheme.GREEN)
    private val _bgIndex = MutableStateFlow(0)
    override val appTheme: Flow<AppTheme> = _theme
    override val backgroundImageIndex: Flow<Int> = _bgIndex
    override suspend fun setAppTheme(theme: AppTheme) {
        _theme.value = theme
    }
    override suspend fun setBackgroundImageIndex(index: Int) {
        _bgIndex.value = index
    }
}

class FakeListNoteadRepository : NoteadRepository {
    val deletedFolders = mutableListOf<Long>()
    val deletedNotes = mutableListOf<Long>()

    val itemsFlow = MutableStateFlow<List<FolderOrNote>>(emptyList())

    override fun getFoldersAndNotes(parentId: Long?): Flow<List<FolderOrNote>> = itemsFlow
    override fun getAllFoldersAndNotes(): Flow<List<FolderOrNote>> = itemsFlow
    override fun observeFolder(id: Long): Flow<Folder?> = kotlinx.coroutines.flow.flowOf(null)
    override suspend fun getFolderById(id: Long): Folder? = null
    override suspend fun getNoteWithText(noteId: Long): NoteWithText? = null
    override suspend fun upsertFolder(folder: Folder): Long = 0L
    override suspend fun deleteFolder(folderId: Long) {
        deletedFolders.add(folderId)
    }
    override suspend fun saveNoteWithContent(note: Note, content: String): Long = 0L
    override suspend fun deleteNote(noteId: Long) {
        deletedNotes.add(noteId)
    }
}

class FakeBackupRepository : com.trodar.domain.repository.BackupRepository {
    override suspend fun exportBackup(): ByteArray = byteArrayOf()
    override suspend fun importBackup(bytes: ByteArray): Result<Unit> = Result.success(Unit)
}

class FakeFileExporter : com.trodar.util.FileExporter {
    override suspend fun saveBackupToDestination(destinationUriOrPath: String, bytes: ByteArray): String = destinationUriOrPath
}

@OptIn(ExperimentalCoroutinesApi::class)
class NoteadListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun testPendingDeletionHidesItemFromList() = runTest {
        val repository = FakeListNoteadRepository()
        val settingsRepo = FakeSettingsRepository()
        val folder = Folder(id = 1L, name = "Папка 1", createdAt = 0L, updatedAt = 0L)
        val note = Note(id = 2L, title = "Запись 1", createdAt = 0L, updatedAt = 0L)
        val folderItem = FolderOrNote.FolderItem(folder)
        val noteItem = FolderOrNote.NoteItem(note)

        repository.itemsFlow.value = listOf(folderItem, noteItem)

        val viewModel = NoteadListViewModel(repository, settingsRepo, FakeBackupRepository(), FakeFileExporter())
        backgroundScope.launch { viewModel.items.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.items.value.size)

        viewModel.deleteItemPending(folderItem)
        testDispatcher.scheduler.advanceTimeBy(100)

        val pending = viewModel.pendingDeletion.value
        assertTrue(pending is PendingDeletion.Folder)
        assertEquals(1L, pending.id)
        assertEquals("Папка 1", pending.name)

        assertEquals(1, viewModel.items.value.size)
        assertEquals(noteItem, viewModel.items.value.first())

        assertTrue(repository.deletedFolders.isEmpty())
    }

    @Test
    fun testUndoDeleteRestoresItemToList() = runTest {
        val repository = FakeListNoteadRepository()
        val settingsRepo = FakeSettingsRepository()
        val folder = Folder(id = 1L, name = "Папка 1", createdAt = 0L, updatedAt = 0L)
        val folderItem = FolderOrNote.FolderItem(folder)

        repository.itemsFlow.value = listOf(folderItem)

        val viewModel = NoteadListViewModel(repository, settingsRepo, FakeBackupRepository(), FakeFileExporter())
        backgroundScope.launch { viewModel.items.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.deleteItemPending(folderItem)
        testDispatcher.scheduler.advanceTimeBy(100)
        assertEquals(0, viewModel.items.value.size)

        viewModel.undoDelete()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.pendingDeletion.value)
        assertEquals(1, viewModel.items.value.size)
        assertTrue(repository.deletedFolders.isEmpty())
    }

    @Test
    fun testAutoCommitAfter15Seconds() = runTest {
        val repository = FakeListNoteadRepository()
        val settingsRepo = FakeSettingsRepository()
        val note = Note(id = 2L, title = "Запись 1", createdAt = 0L, updatedAt = 0L)
        val noteItem = FolderOrNote.NoteItem(note)

        repository.itemsFlow.value = listOf(noteItem)

        val viewModel = NoteadListViewModel(repository, settingsRepo, FakeBackupRepository(), FakeFileExporter())
        backgroundScope.launch { viewModel.items.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.deleteItemPending(noteItem)
        testDispatcher.scheduler.advanceTimeBy(100)
        assertTrue(repository.deletedNotes.isEmpty())

        testDispatcher.scheduler.advanceTimeBy(15_000)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf(2L), repository.deletedNotes)
        assertNull(viewModel.pendingDeletion.value)
    }

    @Test
    fun testNewPendingDeletionCommitsPrevious() = runTest {
        val repository = FakeListNoteadRepository()
        val settingsRepo = FakeSettingsRepository()
        val folderItem = FolderOrNote.FolderItem(Folder(id = 1L, name = "Папка", createdAt = 0L, updatedAt = 0L))
        val noteItem = FolderOrNote.NoteItem(Note(id = 2L, title = "Запись", createdAt = 0L, updatedAt = 0L))

        repository.itemsFlow.value = listOf(folderItem, noteItem)

        val viewModel = NoteadListViewModel(repository, settingsRepo, FakeBackupRepository(), FakeFileExporter())
        backgroundScope.launch { viewModel.items.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.deleteItemPending(folderItem)
        testDispatcher.scheduler.advanceTimeBy(100)

        viewModel.deleteItemPending(noteItem)
        testDispatcher.scheduler.advanceTimeBy(100)

        assertEquals(listOf(1L), repository.deletedFolders)
        val pending = viewModel.pendingDeletion.value
        assertTrue(pending is PendingDeletion.Note)
        assertEquals(2L, pending.id)
    }

    @Test
    fun testCreateFolderCommitsPendingDeletion() = runTest {
        val repository = FakeListNoteadRepository()
        val settingsRepo = FakeSettingsRepository()
        val folderItem = FolderOrNote.FolderItem(Folder(id = 1L, name = "Папка", createdAt = 0L, updatedAt = 0L))

        repository.itemsFlow.value = listOf(folderItem)

        val viewModel = NoteadListViewModel(repository, settingsRepo, FakeBackupRepository(), FakeFileExporter())
        backgroundScope.launch { viewModel.items.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.deleteItemPending(folderItem)
        testDispatcher.scheduler.advanceTimeBy(100)

        viewModel.createFolder("Новая папка")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf(1L), repository.deletedFolders)
        assertNull(viewModel.pendingDeletion.value)
    }

    @Test
    fun testSetBackgroundIndex() = runTest {
        val repository = FakeListNoteadRepository()
        val settingsRepo = FakeSettingsRepository()
        val viewModel = NoteadListViewModel(repository, settingsRepo, FakeBackupRepository(), FakeFileExporter())
        backgroundScope.launch { viewModel.currentBackgroundIndex.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, viewModel.currentBackgroundIndex.value)

        viewModel.setBackgroundIndex(3)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(3, viewModel.currentBackgroundIndex.value)
    }
}
