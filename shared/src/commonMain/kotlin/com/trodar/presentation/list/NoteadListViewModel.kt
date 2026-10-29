package com.trodar.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trodar.domain.model.Folder
import com.trodar.domain.model.FolderOrNote
import com.trodar.domain.model.Note
import com.trodar.domain.model.AppTheme
import com.trodar.domain.repository.NoteadRepository
import com.trodar.domain.repository.SettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.Clock

@OptIn(ExperimentalCoroutinesApi::class)
class NoteadListViewModel(
    private val repository: NoteadRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val currentTheme: StateFlow<AppTheme> = settingsRepository.appTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppTheme.LIGHT)

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingsRepository.setAppTheme(theme)
        }
    }

    private val _folderStack = MutableStateFlow<List<Pair<Long?, String>>>(listOf(null to "Блокнот"))

    val parentId: StateFlow<Long?> = _folderStack
        .map { it.last().first }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val currentFolderName: StateFlow<String> = _folderStack
        .map { it.last().second }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "Блокнот")

    val canNavigateBack: StateFlow<Boolean> = _folderStack
        .map { it.size > 1 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val items: StateFlow<List<FolderOrNote>> = parentId
        .flatMapLatest { id ->
            repository.getFoldersAndNotes(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun navigateToFolder(folderId: Long?, folderName: String) {
        _folderStack.update { stack -> stack + (folderId to folderName) }
    }

    fun navigateBack() {
        if (_folderStack.value.size > 1) {
            _folderStack.update { stack -> stack.dropLast(1) }
        }
    }

    fun createFolder(name: String) {
        viewModelScope.launch {
            val now = Clock.System.now().toEpochMilliseconds()
            val folder = Folder(
                parentId = parentId.value,
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
            folderId = parentId.value,
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
