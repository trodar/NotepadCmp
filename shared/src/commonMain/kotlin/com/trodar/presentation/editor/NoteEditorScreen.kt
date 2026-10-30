package com.trodar.presentation.editor

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    noteId: Long,
    folderId: Long? = null,
    viewModel: NoteEditorViewModel = koinViewModel(parameters = { parametersOf(noteId, folderId) }),
    onBack: () -> Unit
) {
    val note by viewModel.note.collectAsState()

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text(note?.title.takeIf { !it.isNullOrEmpty() } ?: "Запись") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.saveNow()
                        onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.saveNow()
                    }) {
                        Icon(Icons.Default.Done, contentDescription = "Сохранить")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .padding(horizontal = 16.dp)
        ) {
            RichTextEditor(
                state = viewModel.richTextState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            )
        }
    }
}
