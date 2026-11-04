package com.trodar.presentation.list

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.trodar.domain.model.Folder
import com.trodar.domain.model.FolderOrNote
import com.trodar.domain.model.Note
import com.trodar.domain.model.getBackgroundResource
import com.trodar.presentation.settings.SettingsDrawerContent
import com.trodar.presentation.theme.IconThemeType
import com.trodar.presentation.theme.ThemedIcon
import com.trodar.presentation.theme.ThemedText
import com.trodar.presentation.theme.defaultCardColors
import com.trodar.presentation.theme.defaultTopAppBarColors
import com.trodar.util.rememberOpenFileLauncher
import com.trodar.util.rememberSaveFileLauncher
import kotlinx.coroutines.launch
import notead.shared.generated.resources.Res
import notead.shared.generated.resources.flower4
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt
import kotlin.time.Clock

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
    val currentBackgroundIndex by viewModel.currentBackgroundIndex.collectAsState()
    val parentId by viewModel.parentId.collectAsState()
    val pendingDeletion by viewModel.pendingDeletion.collectAsState()

    LaunchedEffect(isInitialDataLoaded) {
        if (isInitialDataLoaded) {
            onInitialDataLoaded()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var showFolderDialog by remember { mutableStateOf(false) }
    var folderToRename by remember { mutableStateOf<Folder?>(null) }
    var folderNameInput by remember { mutableStateOf("") }
    var folderForActionMenu by remember { mutableStateOf<Folder?>(null) }
    var itemToDelete by remember { mutableStateOf<FolderOrNote?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pendingDeletion) {
        val pending = pendingDeletion
        if (pending != null) {
            val itemLabel = when (pending) {
                is PendingDeletion.Folder -> "Папка"
                is PendingDeletion.Note -> "Запись"
            }
            val result = snackbarHostState.showSnackbar(
                message = "$itemLabel \"${pending.name}\" удалена",
                actionLabel = "Отменить",
                duration = SnackbarDuration.Indefinite
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoDelete()
            }
        } else {
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }

    val bgResource = currentTheme.getBackgroundResource(currentBackgroundIndex)

    val saveFileLauncher = rememberSaveFileLauncher { destination ->
        if (!destination.isNullOrBlank()) {
            viewModel.exportBackup(destination) { result ->
                coroutineScope.launch {
                    if (result.isSuccess) {
                        snackbarHostState.showSnackbar("Резервная копия сохранена")
                    } else {
                        snackbarHostState.showSnackbar("Ошибка экспорта: ${result.exceptionOrNull()?.message ?: "Неизвестная ошибка"}")
                    }
                }
            }
        }
    }

    val openFileLauncher = rememberOpenFileLauncher { bytes ->
        if (bytes != null && bytes.isNotEmpty()) {
            viewModel.importBackup(bytes) { result ->
                coroutineScope.launch {
                    if (result.isSuccess) {
                        snackbarHostState.showSnackbar("Данные успешно восстановлены")
                    } else {
                        snackbarHostState.showSnackbar("Ошибка импорта: ${result.exceptionOrNull()?.message ?: "Неизвестная ошибка"}")
                    }
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(320.dp),
                drawerContainerColor = Color.Transparent,
                windowInsets = WindowInsets(0.dp)
            ) {
                SettingsDrawerContent(
                    currentTheme = currentTheme,
                    currentBackgroundIndex = currentBackgroundIndex,
                    onSelectTheme = { theme ->
                        viewModel.setTheme(theme)
                    },
                    onSelectBackgroundIndex = { index ->
                        viewModel.setBackgroundIndex(index)
                    },
                    onExportBackup = {
                        val timestamp = Clock.System.now().toEpochMilliseconds()
                        saveFileLauncher.launch("notead_backup_$timestamp.zip")
                    },
                    onImportBackup = {
                        openFileLauncher.launch()
                    }
                )
            }
        }
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                    Snackbar(
                        snackbarData = snackbarData,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionColor = MaterialTheme.colorScheme.primary
                    )
                }
            },
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = if (bgResource != null) Color.Transparent else MaterialTheme.colorScheme.background,
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
                        IconButton(onClick = {
                            viewModel.commitPendingDeletion()
                            folderToRename = null
                            folderNameInput = ""
                            showFolderDialog = true
                        }) {
                            Icon(Icons.Default.Folder, contentDescription = "Создать папку")
                        }
                        IconButton(onClick = {
                            viewModel.commitPendingDeletion()
                            onNavigateToNote(0L, parentId)
                        }) {
                            Icon(
                                painter = painterResource(Res.drawable.flower4),
                                contentDescription = "Создать запись",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize()) {
                if (bgResource != null) {
                    Image(
                        painter = painterResource(bgResource),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )
                }
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
                        SwipeToDeleteItem(
                            itemKey = when (item) {
                                is FolderOrNote.FolderItem -> item.folder.id
                                is FolderOrNote.NoteItem -> item.note.id
                            },
                            onDelete = {
                                viewModel.deleteItemPending(item)
                            }
                        ) {
                            when (item) {
                                is FolderOrNote.FolderItem -> {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .combinedClickable(
                                                onClick = {
                                                    viewModel.navigateToFolder(item.folder.id, item.folder.name)
                                                },
                                                onLongClick = {
                                                    viewModel.commitPendingDeletion()
                                                    folderForActionMenu = item.folder
                                                }
                                            ),
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
                                        }
                                    }
                                }
                                is FolderOrNote.NoteItem -> {
                                    val noteBgColorInt = item.note.bgColor
                                    val hasCustomBg = noteBgColorInt != 0 && noteBgColorInt != Note.DEFAULT_BG_COLOR
                                    val cardColors = if (hasCustomBg) {
                                        CardDefaults.cardColors(containerColor = Color(noteBgColorInt))
                                    } else {
                                        defaultCardColors()
                                    }
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .combinedClickable(
                                                onClick = {
                                                    viewModel.commitPendingDeletion()
                                                    onNavigateToNote(item.note.id, item.note.folderId)
                                                },
                                                onLongClick = {
                                                    viewModel.commitPendingDeletion()
                                                    itemToDelete = item
                                                }
                                            ),
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
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (folderForActionMenu != null) {
                val targetFolder = folderForActionMenu!!
                AlertDialog(
                    onDismissRequest = { folderForActionMenu = null },
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    textContentColor = MaterialTheme.colorScheme.onSurface,
                    title = {
                        Text(
                            text = targetFolder.name.ifBlank { "Без названия" },
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Surface(
                                onClick = {
                                    folderToRename = targetFolder
                                    folderNameInput = targetFolder.name
                                    showFolderDialog = true
                                    folderForActionMenu = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium,
                                color = Color.Transparent
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp, horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ThemedIcon(
                                        imageVector = Icons.Default.Create,
                                        contentDescription = null,
                                        type = IconThemeType.PRIMARY
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "Переименовать",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            Surface(
                                onClick = {
                                    itemToDelete = FolderOrNote.FolderItem(targetFolder)
                                    folderForActionMenu = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium,
                                color = Color.Transparent
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp, horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ThemedIcon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        type = IconThemeType.ERROR
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "Удалить",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {},
                    dismissButton = {
                        TextButton(onClick = { folderForActionMenu = null }) {
                            Text("Отмена", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                )
            }

            if (showFolderDialog) {
                val isEditing = folderToRename != null
                AlertDialog(
                    onDismissRequest = {
                        showFolderDialog = false
                        folderToRename = null
                        folderNameInput = ""
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    textContentColor = MaterialTheme.colorScheme.onSurface,
                    title = { Text(if (isEditing) "Переименовать папку" else "Новая папка", style = MaterialTheme.typography.titleLarge) },
                    text = {
                        OutlinedTextField(
                            value = folderNameInput,
                            onValueChange = { folderNameInput = it },
                            label = { Text("Имя папки") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val trimmed = folderNameInput.trim()
                            if (trimmed.isNotBlank()) {
                                val currentFolder = folderToRename
                                if (currentFolder != null) {
                                    viewModel.renameFolder(currentFolder, trimmed)
                                } else {
                                    viewModel.createFolder(trimmed)
                                }
                                folderNameInput = ""
                                folderToRename = null
                                showFolderDialog = false
                            }
                        }) {
                            Text(if (isEditing) "Сохранить" else "Создать", color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showFolderDialog = false
                            folderToRename = null
                            folderNameInput = ""
                        }) {
                            Text("Отмена", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                )
            }

            if (itemToDelete != null) {
                val targetItem = itemToDelete
                val name = when (targetItem) {
                    is FolderOrNote.FolderItem -> targetItem.folder.name.ifBlank { "Без названия" }
                    is FolderOrNote.NoteItem -> targetItem.note.title.ifBlank { "Без названия" }
                    null -> ""
                }
                val title = when (targetItem) {
                    is FolderOrNote.FolderItem -> "Удалить папку?"
                    is FolderOrNote.NoteItem -> "Удалить запись?"
                    null -> "Удаление"
                }
                AlertDialog(
                    onDismissRequest = { itemToDelete = null },
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    title = { Text(title, style = MaterialTheme.typography.titleLarge) },
                    text = { Text("Действительно хотите удалить \"$name\"?", style = MaterialTheme.typography.bodyMedium) },
                    confirmButton = {
                        TextButton(onClick = {
                            targetItem?.let { viewModel.deleteItemPending(it) }
                            itemToDelete = null
                        }) {
                            Text("Удалить", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { itemToDelete = null }) {
                            Text("Отмена", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun SwipeToDeleteItem(
    itemKey: Any,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val thresholdPx = remember(density) { with(density) { 140.dp.toPx() } }
    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }

    val currentOffset = offsetX.value
    val isSwipingLeft = currentOffset < 0f
    val isPastThreshold = currentOffset <= -thresholdPx

    val backgroundColor = when {
        isPastThreshold -> MaterialTheme.colorScheme.error.copy(alpha = 0.35f)
        else -> Color.Transparent
    }
    val contentColor = when {
        isPastThreshold -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
    }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(backgroundColor, shape = MaterialTheme.shapes.medium)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (isSwipingLeft) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = contentColor
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(itemKey) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            val finalOffset = offsetX.value
                            coroutineScope.launch {
                                offsetX.animateTo(
                                    targetValue = 0f,
                                    animationSpec = tween(durationMillis = 200)
                                )
                            }
                            if (finalOffset <= -thresholdPx) {
                                onDelete()
                            }
                        },
                        onDragCancel = {
                            coroutineScope.launch {
                                offsetX.animateTo(0f, animationSpec = tween(durationMillis = 200))
                            }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            coroutineScope.launch {
                                val newOffset = (offsetX.value + dragAmount).coerceAtMost(0f)
                                offsetX.snapTo(newOffset)
                            }
                        }
                    )
                }
        ) {
            content()
        }
    }
}
