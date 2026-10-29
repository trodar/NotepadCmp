package com.trodar.notead

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.trodar.presentation.editor.NoteEditorScreen
import com.trodar.presentation.editor.NoteEditorViewModel
import com.trodar.presentation.list.NoteadListScreen
import com.trodar.presentation.list.NoteadListViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

sealed interface Screen {
    data object List : Screen
    data class Editor(val noteId: Long, val folderId: Long?) : Screen
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.List) }

        when (val screen = currentScreen) {
            is Screen.List -> {
                val viewModel = koinViewModel<NoteadListViewModel>()
                NoteadListScreen(
                    viewModel = viewModel,
                    onNavigateToNote = { noteId ->
                        currentScreen = Screen.Editor(noteId = noteId, folderId = viewModel.parentId.value)
                    }
                )
            }
            is Screen.Editor -> {
                val viewModel = koinViewModel<NoteEditorViewModel>(
                    parameters = { parametersOf(screen.noteId, screen.folderId) }
                )
                NoteEditorScreen(
                    viewModel = viewModel,
                    onBack = {
                        currentScreen = Screen.List
                    }
                )
            }
        }
    }
}
