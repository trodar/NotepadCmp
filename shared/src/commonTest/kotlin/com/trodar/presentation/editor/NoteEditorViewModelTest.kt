package com.trodar.presentation.editor

import androidx.compose.runtime.snapshots.Snapshot
import androidx.lifecycle.SavedStateHandle
import com.trodar.domain.model.Folder
import com.trodar.domain.model.FolderOrNote
import com.trodar.domain.model.Note
import com.trodar.domain.model.NoteWithText
import com.trodar.domain.repository.NoteadRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FakeNoteadRepository : NoteadRepository {
    val savedNotes = mutableMapOf<Long, NoteWithText>()
    private var nextId = 1L

    override fun getFoldersAndNotes(parentId: Long?): Flow<List<FolderOrNote>> = flowOf(emptyList())
    override fun getAllFoldersAndNotes(): Flow<List<FolderOrNote>> = flowOf(emptyList())
    override fun observeFolder(id: Long): Flow<Folder?> = flowOf(null)
    override suspend fun getFolderById(id: Long): Folder? = null

    override suspend fun getNoteWithText(noteId: Long): NoteWithText? {
        return savedNotes[noteId]
    }

    override suspend fun upsertFolder(folder: Folder): Long = 0L
    override suspend fun deleteFolder(folderId: Long) {}

    override suspend fun saveNoteWithContent(note: Note, content: String): Long {
        val id = if (note.id > 0) note.id else nextId++
        val updatedNote = note.copy(id = id)
        savedNotes[id] = NoteWithText(updatedNote, content)
        return id
    }

    override suspend fun deleteNote(noteId: Long) {
        savedNotes.remove(noteId)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class NoteEditorViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun testInitialStateIsNotModified() = runTest {
        val repository = FakeNoteadRepository()
        val viewModel = NoteEditorViewModel(
            savedStateHandle = SavedStateHandle(),
            noteId = 0L,
            folderId = null,
            repository = repository
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.isModified.value)
    }

    @Test
    fun testTextChangeMarksAsModifiedAndAutoSavesAfter1Second() = runTest {
        val repository = FakeNoteadRepository()
        val viewModel = NoteEditorViewModel(
            savedStateHandle = SavedStateHandle(),
            noteId = 0L,
            folderId = null,
            repository = repository
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.isModified.value)

        Snapshot.withMutableSnapshot {
            viewModel.richTextState.setHtml("<p>Test Note Content</p>")
        }
        Snapshot.sendApplyNotifications()
        testDispatcher.scheduler.advanceTimeBy(100)

        assertTrue(viewModel.isModified.value)

        testDispatcher.scheduler.advanceTimeBy(1000)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.isModified.value)
        assertEquals(1, repository.savedNotes.size)
        assertEquals("<p>Test Note Content</p>", repository.savedNotes[1L]?.content)
    }

    @Test
    fun testManualSaveResetsIsModifiedImmediately() = runTest {
        val repository = FakeNoteadRepository()
        val viewModel = NoteEditorViewModel(
            savedStateHandle = SavedStateHandle(),
            noteId = 0L,
            folderId = null,
            repository = repository
        )
        testDispatcher.scheduler.advanceUntilIdle()

        Snapshot.withMutableSnapshot {
            viewModel.richTextState.setHtml("<p>Manual Save Test</p>")
        }
        Snapshot.sendApplyNotifications()
        testDispatcher.scheduler.advanceTimeBy(100)

        assertTrue(viewModel.isModified.value)

        viewModel.saveNow()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.isModified.value)
        assertEquals(1, repository.savedNotes.size)
        assertEquals("<p>Manual Save Test</p>", repository.savedNotes[1L]?.content)
    }

    @Test
    fun testUpdateBgColorSavesImmediately() = runTest {
        val repository = FakeNoteadRepository()
        val viewModel = NoteEditorViewModel(
            savedStateHandle = SavedStateHandle(),
            noteId = 0L,
            folderId = null,
            repository = repository
        )
        testDispatcher.scheduler.advanceUntilIdle()

        Snapshot.withMutableSnapshot {
            viewModel.richTextState.setHtml("<p>Bg Color Test</p>")
        }
        Snapshot.sendApplyNotifications()
        testDispatcher.scheduler.advanceTimeBy(100)

        viewModel.updateNoteBgColor(0xFF123456.toInt())
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.isModified.value)
        assertEquals(1, repository.savedNotes.size)
        assertEquals(0xFF123456.toInt(), repository.savedNotes[1L]?.note?.bgColor)
    }
}
