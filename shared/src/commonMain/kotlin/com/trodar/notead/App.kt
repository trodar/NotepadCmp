package com.trodar.notead

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.trodar.domain.model.AppTheme
import com.trodar.domain.repository.SettingsRepository
import com.trodar.presentation.editor.NoteEditorScreen
import com.trodar.presentation.editor.NoteEditorViewModel
import com.trodar.presentation.list.NoteadListScreen
import com.trodar.presentation.list.NoteadListViewModel
import com.trodar.presentation.theme.NoteadTheme
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

sealed interface Screen {
    data object List : Screen
    data class Editor(val noteId: Long, val folderId: Long? = null) : Screen
}

@Composable
@Preview
fun App() {
    val settingsRepository = koinInject<SettingsRepository>()
    val currentTheme by settingsRepository.appTheme.collectAsState(initial = AppTheme.LIGHT)

    NoteadTheme(appTheme = currentTheme) {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.List) }

        when (val screen = currentScreen) {
            is Screen.List -> {
                NoteadListScreen(
                    onNavigateToNote = { noteId ->
                        currentScreen = Screen.Editor(noteId = noteId)
                    }
                )
            }
            is Screen.Editor -> {
                NoteEditorScreen(
                    viewModel = koinViewModel { parametersOf(screen.noteId, screen.folderId) },
                    onBack = {
                        currentScreen = Screen.List
                    }
                )
            }
        }
    }
}
