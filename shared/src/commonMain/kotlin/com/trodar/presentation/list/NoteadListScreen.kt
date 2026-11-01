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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.trodar.domain.model.FolderOrNote
import com.trodar.presentation.settings.SettingsDrawerContent
import com.trodar.presentation.theme.IconThemeType
import com.trodar.presentation.theme.ThemedIcon
import com.trodar.presentation.theme.ThemedText
import com.trodar.presentation.theme.defaultCardColors
import com.trodar.presentation.theme.defaultTopAppBarColors
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteadListScreen(
    viewModel: NoteadListViewModel = koinViewModel<NoteadListViewModel>(),
    onNavigateToNote: (noteId: Long, folderId: Long?) -> Unit,
    onInitialDataLoaded: () -> Unit = {}
) {
    val items by viewModel.items.collectAsState()
    val isInitialDataLoaded by viewModel.isInitialDataLoaded.collectAsState()
    val canNavigateBack by viewModel.canNavigateBack.collectAsState()
    val currentFolderName by viewModel.currentFolderName.collectAsState()
    val currentTheme by viewModel.currentTheme.collectAsState()
    val parentId by viewModel.parentId.collectAsState()

    LaunchedEffect(isInitialDataLoaded) {
        if (isInitialDataLoaded) {
            onInitialDataLoaded()
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
                drawerContainerColor = Color.Transparent,
                windowInsets = WindowInsets(0.dp)
            ) {
                SettingsDrawerContent(
                    currentTheme = currentTheme,
                    onSelectTheme = { theme ->
                        viewModel.setTheme(theme)
                    }
                )
            }
        }
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                TopAppBar(
                    title = { Text(currentFolderName) },
                    colors = defaultTopAppBarColors(),
                    navigationIcon = {
                        if (canNavigateBack) {
                            IconButton(onClick = {
                                viewModel.navigateBack()
                            }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                            }
                        } else {
                            IconButton(onClick = {
                                coroutineScope.launch { drawerState.open() }
                            }) {
                                Icon(Icons.Default.Menu, contentDescription = "Настройки")
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { showCreateFolderDialog = true }) {
                            Icon(Icons.Default.Folder, contentDescription = "Создать папку")
                        }
                        IconButton(onClick = {
                            onNavigateToNote(0L, parentId)
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
                    .consumeWindowInsets(padding)
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                contentPadding = padding,
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
                                    },
                                colors = defaultCardColors()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ThemedIcon(Icons.Default.Folder, contentDescription = null, type = IconThemeType.PRIMARY)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    ThemedText(
                                        text = item.folder.name.ifBlank { "Без названия" },
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(onClick = { viewModel.deleteFolder(item.folder.id) }) {
                                        ThemedIcon(Icons.Default.Delete, contentDescription = "Удалить папку", type = IconThemeType.ERROR)
                                    }
                                }
                            }
                        }
                        is FolderOrNote.NoteItem -> {
                            val noteBgColorInt = item.note.bgColor
                            val hasCustomBg = noteBgColorInt != 0 && noteBgColorInt != 0xFFFFFFFF.toInt()
                            val cardColors = if (hasCustomBg) {
                                CardDefaults.cardColors(containerColor = Color(noteBgColorInt))
                            } else {
                                defaultCardColors()
                            }
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onNavigateToNote(item.note.id, item.note.folderId)
                                    },
                                colors = cardColors
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ThemedIcon(Icons.Default.Create, contentDescription = null, type = IconThemeType.SECONDARY)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        ThemedText(
                                            text = item.note.title.ifBlank { "Без названия" },
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                    IconButton(onClick = { viewModel.deleteNote(item.note.id) }) {
                                        ThemedIcon(Icons.Default.Delete, contentDescription = "Удалить запись", type = IconThemeType.ERROR)
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
                            if (newFolderName.trim().isNotBlank()) {
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
}
