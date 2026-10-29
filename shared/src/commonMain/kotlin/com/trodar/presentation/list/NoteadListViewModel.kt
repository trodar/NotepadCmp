package com.trodar.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trodar.domain.model.Folder
import com.trodar.domain.model.FolderOrNote
import com.trodar.domain.model.Note
import com.trodar.domain.repository.NoteadRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.Clock

@OptIn(ExperimentalCoroutinesApi::class)
class NoteadListViewModel(
    private val repository: NoteadRepository
) : ViewModel() {

    private val _parentId = MutableStateFlow<Long?>(null)
    val parentId: StateFlow<Long?> = _parentId.asStateFlow()

    private val _currentFolderName = MutableStateFlow<String>("Блокнот")
    val currentFolderName: StateFlow<String> = _currentFolderName.asStateFlow()

    val items: StateFlow<List<FolderOrNote>> = _parentId
        .flatMapLatest { id ->
            repository.getFoldersAndNotes(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun navigateToFolder(folderId: Long?, folderName: String) {
        _parentId.value = folderId
        _currentFolderName.value = folderName
    }

    fun createFolder(name: String) {
        viewModelScope.launch {
            val now = Clock.System.now().toEpochMilliseconds()
            val folder = Folder(
                parentId = _parentId.value,
                name = name,
                createdAt = now,
                updatedAt = now
            )
            repository.upsertFolder(folder)
        }
    }

    suspend fun createNewNote(): Long {
        val now = Clock.System.now().toEpochMilliseconds()
        val note = Note(
            folderId = _parentId.value,
            title = "",
            createdAt = now,
            updatedAt = now
        )
        return repository.saveNoteWithContent(note, "")
    }

    fun deleteFolder(folderId: Long) {
        viewModelScope.launch {
            repository.deleteFolder(folderId)
        }
    }

    fun deleteNote(noteId: Long) {
        viewModelScope.launch {
            repository.deleteNote(noteId)
        }
    }
}
