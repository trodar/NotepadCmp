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
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.trodar.domain.model.Note
import com.trodar.presentation.theme.defaultTopAppBarColors
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.windowInsetsPadding

import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.input.nestedscroll.nestedScroll

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.layout.onGloballyPositioned

private class TextLayoutHolder {
    var layoutResult: TextLayoutResult? = null
}

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
    val layoutHolder = remember { TextLayoutHolder() }
    val focusRequester = remember { FocusRequester() }

    var isTopBarPinnedHidden by rememberSaveable { mutableStateOf(false) }
    var toolbarHeightPx by remember { mutableIntStateOf(0) }

    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)

    val noteBgColorInt = note?.bgColor ?: Note.DEFAULT_BG_COLOR
    val isDefaultBg = noteBgColorInt == 0 || noteBgColorInt == Note.DEFAULT_BG_COLOR
    val noteBgColor = if (isDefaultBg) MaterialTheme.colorScheme.surface else Color(noteBgColorInt)

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(isTopBarPinnedHidden) {
        if (!isTopBarPinnedHidden) {
            scrollBehavior.state.heightOffset = 0f
            scrollState.scrollTo(0)
        }
    }

    LaunchedEffect(viewModel.richTextState.selection, imeBottom) {
        val layout = layoutHolder.layoutResult ?: return@LaunchedEffect
        val selection = viewModel.richTextState.selection
        if (selection.end >= 0 && selection.end <= layout.layoutInput.text.length) {
            val cursorRect = layout.getCursorRect(selection.end)
            val cursorTopInEditor = cursorRect.top.toInt()
            val cursorBottomInEditor = cursorRect.bottom.toInt()
            val viewportHeight = scrollState.viewportSize

            if (viewportHeight > 0) {
                val cursorTopInColumn = toolbarHeightPx + cursorTopInEditor
                val cursorBottomInColumn = toolbarHeightPx + cursorBottomInEditor

                val cursorHeight = (cursorBottomInEditor - cursorTopInEditor).coerceAtLeast(0)
                val defaultPadding = with(density) { 24.dp.toPx().toInt() }
                val maxPadding = ((viewportHeight - cursorHeight) / 2).coerceAtLeast(0)
                val extraPadding = defaultPadding.coerceAtMost(maxPadding)

                val currentScroll = scrollState.value
                val visibleBottom = currentScroll + viewportHeight

                if (cursorBottomInColumn + extraPadding > visibleBottom) {
                    val targetScroll = (cursorBottomInColumn + extraPadding - viewportHeight)
                        .coerceAtMost(cursorTopInColumn)
                        .coerceIn(0, scrollState.maxValue)
                    if (targetScroll > currentScroll) {
                        scrollState.animateScrollTo(targetScroll)
                    }
                } else if (cursorTopInColumn - extraPadding < currentScroll) {
                    val minAllowedScroll = if (currentScroll >= toolbarHeightPx) toolbarHeightPx else 0
                    val targetScroll = (cursorTopInColumn - extraPadding)
                        .coerceAtLeast(minAllowedScroll)
                        .coerceIn(0, scrollState.maxValue)
                    if (targetScroll < currentScroll) {
                        scrollState.animateScrollTo(targetScroll)
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            if (!isTopBarPinnedHidden) {
                TopAppBar(
                    title = { Text(note?.title.takeIf { !it.isNullOrEmpty() } ?: "Запись") },
                    colors = defaultTopAppBarColors(),
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)),
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
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        toolbarHeightPx = coordinates.size.height
                    }
                ) {
                    Column {
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
                            },
                            isTopBarHidden = isTopBarPinnedHidden,
                            onToggleTopBarHidden = {
                                isTopBarPinnedHidden = !isTopBarPinnedHidden
                            }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                }

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
                            textColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        onTextLayout = { result ->
                            layoutHolder.layoutResult = result
                        }
                    )
                }
            }
        }
    }
}
