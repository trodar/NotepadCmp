package com.trodar.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.trodar.domain.model.AppTheme
import com.trodar.domain.repository.SettingsRepository
import com.trodar.presentation.editor.NoteEditorScreen
import com.trodar.presentation.list.NoteadListScreen
import com.trodar.presentation.theme.NoteadTheme
import org.koin.compose.koinInject

@Composable
fun NavHost(
    onDataReady: () -> Unit = {}
) {
    val settingsRepository: SettingsRepository = koinInject()
    val currentTheme by settingsRepository.appTheme.collectAsState(initial = AppTheme.LIGHT)

    NoteadTheme(appTheme = currentTheme) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val startRoute = remember { NoteListRoute }

            val navState = rememberNavState(
                startRoute = startRoute,
                topLevelRoutes = setOf(
                    NoteListRoute
                ),
                primaryTopLevelRoute = NoteListRoute
            )

            val navigator = remember(navState) {
                Navigator(navState, true)
            }

            val entryProvider = entryProvider {
                screens(
                    navigator = navigator,
                    onBack = { navigator.goBack() },
                    onInitialDataLoaded = onDataReady
                )
            }

            NavScaffold(
                navState = navState,
                navigator = navigator,
            ) {
                NavDisplay(
                    entries = navState.toDecorationEntries(entryProvider),
                    onBack = navigator::goBack,
                )
            }
        }
    }
}

private fun EntryProviderScope<AppRoute>.screens(
    navigator: Navigator,
    onBack: () -> Unit,
    onInitialDataLoaded: () -> Unit,
) {
    entry<NoteListRoute> {
        NoteadListScreen(
            onNavigateToNote = { noteId, folderId ->
                navigator.add(NoteEditorRoute(noteId = noteId, folderId = folderId))
            },
            onInitialDataLoaded = onInitialDataLoaded
        )
    }

    entry<NoteEditorRoute> { route ->
        NoteEditorScreen(
            noteId = route.noteId,
            folderId = route.folderId,
            onBack = onBack
        )
    }
}
