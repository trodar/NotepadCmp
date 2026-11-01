package com.trodar.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trodar.domain.model.Folder
import com.trodar.domain.model.FolderOrNote
import com.trodar.domain.model.AppTheme
import com.trodar.domain.repository.NoteadRepository
import com.trodar.domain.repository.SettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Clock

sealed interface PendingDeletion {
    val id: Long
    val name: String

    data class Folder(
        override val id: Long,
        override val name: String
    ) : PendingDeletion

    data class Note(
        override val id: Long,
        override val name: String
    ) : PendingDeletion
}

@OptIn(ExperimentalCoroutinesApi::class)
class NoteadListViewModel(
    private val repository: NoteadRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val currentTheme: StateFlow<AppTheme> = settingsRepository.appTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppTheme.GREEN)

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingsRepository.setAppTheme(theme)
        }
    }

    private val _folderStack = MutableStateFlow<List<Long?>>(listOf(null))

    val parentId: StateFlow<Long?> = _folderStack
        .map { it.last() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val currentFolderName: StateFlow<String> = parentId
        .flatMapLatest { id ->
            if (id == null) {
                flowOf("Блокнот")
            } else {
                repository.observeFolder(id).map { it?.name?.ifBlank { "Папка" } ?: "Папка" }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "Блокнот")

    val canNavigateBack: StateFlow<Boolean> = _folderStack
        .map { it.size > 1 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _isInitialDataLoaded = MutableStateFlow(false)
    val isInitialDataLoaded: StateFlow<Boolean> = _isInitialDataLoaded.asStateFlow()

    private val _pendingDeletion = MutableStateFlow<PendingDeletion?>(null)
    val pendingDeletion: StateFlow<PendingDeletion?> = _pendingDeletion.asStateFlow()

    private var pendingDeleteJob: Job? = null

    val items: StateFlow<List<FolderOrNote>> = combine(
        parentId.flatMapLatest { id ->
            repository.getFoldersAndNotes(id)
                .onEach { _isInitialDataLoaded.value = true }
        },
        _pendingDeletion
    ) { list, pending ->
        if (pending == null) {
            list
        } else {
            list.filter { item ->
                when (item) {
                    is FolderOrNote.FolderItem -> !(pending is PendingDeletion.Folder && pending.id == item.folder.id)
                    is FolderOrNote.NoteItem -> !(pending is PendingDeletion.Note && pending.id == item.note.id)
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun navigateToFolder(folderId: Long?, folderName: String = "") {
        commitPendingDeletion()
        _folderStack.update { stack -> stack + folderId }
    }

    fun navigateBack() {
        commitPendingDeletion()
        if (_folderStack.value.size > 1) {
            _folderStack.update { stack -> stack.dropLast(1) }
        }
    }

    fun createFolder(name: String) {
        commitPendingDeletion()
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) return
        viewModelScope.launch {
            val now = Clock.System.now().toEpochMilliseconds()
            val folder = Folder(
                parentId = parentId.value,
                name = trimmedName,
                createdAt = now,
                updatedAt = now
            )
            repository.upsertFolder(folder)
        }
    }

    fun renameFolder(folder: Folder, newName: String) {
        commitPendingDeletion()
        val trimmedName = newName.trim()
        if (trimmedName.isBlank()) return
        viewModelScope.launch {
            val now = Clock.System.now().toEpochMilliseconds()
            val updatedFolder = folder.copy(
                name = trimmedName,
                updatedAt = now
            )
            repository.upsertFolder(updatedFolder)
        }
    }

    fun deleteItemPending(item: FolderOrNote) {
        commitPendingDeletion()

        val pending = when (item) {
            is FolderOrNote.FolderItem -> PendingDeletion.Folder(
                id = item.folder.id,
                name = item.folder.name.ifBlank { "Без названия" }
            )
            is FolderOrNote.NoteItem -> PendingDeletion.Note(
                id = item.note.id,
                name = item.note.title.ifBlank { "Без названия" }
            )
        }

        _pendingDeletion.value = pending

        pendingDeleteJob = viewModelScope.launch {
            delay(15_000L)
            commitPendingDeletionInternal()
        }
    }

    fun undoDelete() {
        pendingDeleteJob?.cancel()
        pendingDeleteJob = null
        _pendingDeletion.value = null
    }

    fun commitPendingDeletion() {
        pendingDeleteJob?.cancel()
        pendingDeleteJob = null
        commitPendingDeletionInternal()
    }

    private fun commitPendingDeletionInternal() {
        val pending = _pendingDeletion.value ?: return
        _pendingDeletion.value = null
        viewModelScope.launch {
            withContext(NonCancellable) {
                when (pending) {
                    is PendingDeletion.Folder -> repository.deleteFolder(pending.id)
                    is PendingDeletion.Note -> repository.deleteNote(pending.id)
                }
            }
        }
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

    override fun onCleared() {
        super.onCleared()
        commitPendingDeletion()
    }
}

