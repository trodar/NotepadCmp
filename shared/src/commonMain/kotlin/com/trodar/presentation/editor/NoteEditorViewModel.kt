package com.trodar.presentation.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trodar.domain.model.Note
import com.trodar.domain.repository.NoteadRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

class NoteEditorViewModel(
    private val repository: NoteadRepository,
    private val noteId: Long,
    private val folderId: Long?
) : ViewModel() {

    private val _note = MutableStateFlow<Note?>(null)
    val note: StateFlow<Note?> = _note.asStateFlow()

    private val _content = MutableStateFlow<String>("")
    val content: StateFlow<String> = _content.asStateFlow()

    private var autoSaveJob: Job? = null

    init {
        loadNote()
    }

    private fun loadNote() {
        viewModelScope.launch {
            if (noteId > 0) {
                val noteWithText = repository.getNoteWithText(noteId)
                if (noteWithText != null) {
                    _note.value = noteWithText.note
                    _content.value = noteWithText.content
                }
            } else {
                val now = Clock.System.now().toEpochMilliseconds()
                val newNote = Note(
                    folderId = folderId,
                    title = "",
                    createdAt = now,
                    updatedAt = now
                )
                _note.value = newNote
                _content.value = ""
            }
        }
    }

    fun onContentChanged(newContent: String) {
        _content.value = newContent

        // Debounce auto-save: 1 second
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(1.seconds)
            saveNow()
        }
    }

    fun saveNow() {
        viewModelScope.launch {
            val currentNote = _note.value ?: Note(
                folderId = folderId,
                createdAt = Clock.System.now().toEpochMilliseconds(),
                updatedAt = Clock.System.now().toEpochMilliseconds()
            )
            val currentContent = _content.value
            val savedId = repository.saveNoteWithContent(currentNote, currentContent)
            if (currentNote.id == 0L) {
                _note.value = repository.getNoteWithText(savedId)?.note
            }
        }
    }
}
