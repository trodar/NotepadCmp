package com.trodar.presentation.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import com.trodar.presentation.theme.defaultTopAppBarColors
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
    val scrollState = rememberScrollState()
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)

    LaunchedEffect(viewModel.richTextState.selection, imeBottom, textLayoutResult) {
        val layout = textLayoutResult ?: return@LaunchedEffect
        val selection = viewModel.richTextState.selection
        if (selection.end >= 0 && selection.end <= layout.layoutInput.text.length) {
            val cursorRect = layout.getCursorRect(selection.end)
            val cursorTop = cursorRect.top.toInt()
            val cursorBottom = cursorRect.bottom.toInt()
            val viewportHeight = scrollState.viewportSize

            if (viewportHeight > 0) {
                val extraPadding = with(density) { 24.dp.toPx().toInt() }
                if (cursorBottom + extraPadding > scrollState.value + viewportHeight) {
                    val targetScroll = (cursorBottom + extraPadding - viewportHeight).coerceIn(0, scrollState.maxValue)
                    scrollState.animateScrollTo(targetScroll)
                } else if (cursorTop - extraPadding < scrollState.value) {
                    val targetScroll = (cursorTop - extraPadding).coerceIn(0, scrollState.maxValue)
                    scrollState.animateScrollTo(targetScroll)
                }
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(note?.title.takeIf { !it.isNullOrEmpty() } ?: "Запись") },
                    colors = defaultTopAppBarColors(),
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
                RichTextToolbar(
                    richTextState = viewModel.richTextState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            val minHeight = maxHeight
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp)
            ) {
                RichTextEditor(
                    state = viewModel.richTextState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = minHeight),
                    colors = RichTextEditorDefaults.richTextEditorColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    onTextLayout = { result ->
                        textLayoutResult = result
                    }
                )
            }
        }
    }
}
