package com.trodar.presentation.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
    val isModified by viewModel.isModified.collectAsState()
    val scrollState = rememberScrollState()
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val focusRequester = remember { FocusRequester() }

    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)

    val noteBgColorInt = note?.bgColor ?: 0xFFFFFFFF.toInt()
    val isDefaultBg = noteBgColorInt == 0 || noteBgColorInt == 0xFFFFFFFF.toInt()
    val noteBgColor = if (isDefaultBg) MaterialTheme.colorScheme.surface else Color(noteBgColorInt)

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
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Сохранить",
                                tint = if (isModified) Color(0xFF29862D) else Color.Black
                            )
                        }
                    }
                )
                RichTextToolbar(
                    richTextState = viewModel.richTextState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    noteBgColor = if (isDefaultBg) Color.Unspecified else noteBgColor,
                    onNoteBgColorSelected = { selectedColor ->
                        val colorInt = if (selectedColor == null || selectedColor == Color.Unspecified) {
                            0xFFFFFFFF.toInt()
                        } else {
                            selectedColor.toArgb()
                        }
                        viewModel.updateNoteBgColor(colorInt)
                    }
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
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = minHeight)
                        .background(
                            color = if (isDefaultBg) MaterialTheme.colorScheme.surface else noteBgColor
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            focusRequester.requestFocus()
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    RichTextEditor(
                        state = viewModel.richTextState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = minHeight)
                            .focusRequester(focusRequester),
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
}
