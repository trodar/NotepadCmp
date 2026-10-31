package com.trodar.presentation.editor

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamedrejeb.richeditor.model.RichTextState
import com.trodar.domain.model.Note
import com.trodar.domain.repository.NoteadRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

class NoteEditorViewModel(
    private val savedStateHandle: SavedStateHandle,
    noteId: Long?,
    folderId: Long?,
    private val repository: NoteadRepository
) : ViewModel() {

    private val effectiveNoteId: Long = noteId ?: savedStateHandle.get<Long>("note_id") ?: 0L
    private val effectiveFolderId: Long? = folderId ?: savedStateHandle.get<Long>("folder_id")

    private val _note = MutableStateFlow<Note?>(null)
    val note: StateFlow<Note?> = _note.asStateFlow()

    val richTextState = RichTextState()

    private var autoSaveJob: Job? = null

    init {
        savedStateHandle["note_id"] = effectiveNoteId
        effectiveFolderId?.let { savedStateHandle["folder_id"] = it }
        loadNote()
        observeTextChangesForAutoSave()
    }

    private fun loadNote() {
        viewModelScope.launch {
            if (effectiveNoteId > 0) {
                val noteWithText = repository.getNoteWithText(effectiveNoteId)
                if (noteWithText != null) {
                    _note.value = noteWithText.note
                    richTextState.setHtml(noteWithText.content)
                }
            } else {
                val now = Clock.System.now().toEpochMilliseconds()
                val newNote = Note(
                    folderId = effectiveFolderId,
                    title = "",
                    createdAt = now,
                    updatedAt = now
                )
                _note.value = newNote
                richTextState.setHtml("")
            }
        }
    }

    private fun observeTextChangesForAutoSave() {
        viewModelScope.launch {
            snapshotFlow { richTextState.toHtml() }
                .drop(1)
                .collect {
                    autoSaveJob?.cancel()
                    autoSaveJob = viewModelScope.launch {
                        delay(1.seconds)
                        saveNow()
                    }
                }
        }
    }

    fun saveNow() {
        autoSaveJob?.cancel()

        val currentContent = richTextState.toHtml()
        val currentNote = _note.value ?: Note(
            folderId = effectiveFolderId,
            createdAt = Clock.System.now().toEpochMilliseconds(),
            updatedAt = Clock.System.now().toEpochMilliseconds()
        )

        viewModelScope.launch {
            withContext(NonCancellable) {
                if (isTextBlank(currentContent)) {
                    if (currentNote.id > 0) {
                        repository.deleteNote(currentNote.id)
                        _note.value = currentNote.copy(id = 0)
                    }
                } else {
                    val savedId = repository.saveNoteWithContent(currentNote, currentContent)
                    val updatedNoteWithText = repository.getNoteWithText(savedId)
                    if (updatedNoteWithText != null) {
                        _note.value = updatedNoteWithText.note
                    }
                }
            }
        }
    }

    private fun isTextBlank(html: String): Boolean {
        val cleanText = html
            .replace(Regex("<[^>]*>"), "")
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace(Regex("\\s+"), " ")
            .trim()
        return cleanText.isBlank()
    }
}
