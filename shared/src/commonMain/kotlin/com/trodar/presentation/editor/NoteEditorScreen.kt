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
import com.mohamedrejeb.richeditor.model.rememberRichTextState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    viewModel: NoteEditorViewModel,
    onBack: () -> Unit
) {
    val note by viewModel.note.collectAsState()
    val content by viewModel.content.collectAsState()

    val richTextState = rememberRichTextState()
    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(content) {
        if (!isInitialized && content.isNotEmpty()) {
            richTextState.setHtml(content)
            isInitialized = true
        }
    }

    LaunchedEffect(richTextState.toHtml()) {
        if (isInitialized) {
            viewModel.onContentChanged(richTextState.toHtml())
        }
    }

    // Auto-save on back or dispose
    DisposableEffect(Unit) {
        onDispose {
            viewModel.saveNow()
        }
    }

    Scaffold(
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
                .padding(16.dp)
        ) {
            RichTextEditor(
                state = richTextState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            )
        }
    }
}
