package com.trodar.presentation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trodar.domain.model.FolderOrNote
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteadListScreen(
    viewModel: NoteadListViewModel,
    onNavigateToNote: (Long) -> Unit
) {
    val items by viewModel.items.collectAsState()
    val canNavigateBack by viewModel.canNavigateBack.collectAsState()
    val currentFolderName by viewModel.currentFolderName.collectAsState()

    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentFolderName) },
                navigationIcon = {
                    if (canNavigateBack) {
                        IconButton(onClick = {
                            viewModel.navigateBack()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showCreateFolderDialog = true }) {
                        Icon(Icons.Default.Folder, contentDescription = "Создать папку")
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            val newNoteId = viewModel.createNewNote()
                            onNavigateToNote(newNoteId)
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Создать запись")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items, key = {
                when (it) {
                    is FolderOrNote.FolderItem -> "folder_${it.folder.id}"
                    is FolderOrNote.NoteItem -> "note_${it.note.id}"
                }
            }) { item ->
                when (item) {
                    is FolderOrNote.FolderItem -> {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.navigateToFolder(item.folder.id, item.folder.name)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = item.folder.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { viewModel.deleteFolder(item.folder.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Удалить папку")
                                }
                            }
                        }
                    }
                    is FolderOrNote.NoteItem -> {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onNavigateToNote(item.note.id)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Create, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.note.title.ifEmpty { "Без названия" },
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                IconButton(onClick = { viewModel.deleteNote(item.note.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Удалить запись")
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showCreateFolderDialog) {
            AlertDialog(
                onDismissRequest = { showCreateFolderDialog = false },
                title = { Text("Новая папка") },
                text = {
                    OutlinedTextField(
                        value = newFolderName,
                        onValueChange = { newFolderName = it },
                        label = { Text("Имя папки") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (newFolderName.isNotBlank()) {
                            viewModel.createFolder(newFolderName)
                            newFolderName = ""
                            showCreateFolderDialog = false
                        }
                    }) {
                        Text("Создать")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateFolderDialog = false }) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}
