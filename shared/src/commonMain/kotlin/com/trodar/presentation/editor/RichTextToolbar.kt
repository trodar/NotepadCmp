package com.trodar.presentation.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.RichTextState
import androidx.compose.ui.text.ParagraphStyle

private val textColors = listOf(
    Color(0xFF212121), // Black / Dark Gray
    Color(0xFF757575), // Gray
    Color(0xFFE53935), // Red
    Color(0xFFD81B60), // Pink
    Color(0xFF8E24AA), // Purple
    Color(0xFF1E88E5), // Blue
    Color(0xFF00ACC1), // Cyan
    Color(0xFF43A047), // Green
    Color(0xFFFB8C00), // Orange
    Color(0xFF6D4C41), // Brown
)

private val bgHighlightColors = listOf(
    Color(0xFFFFF59D), // Soft Yellow
    Color(0xFFA5D6A7), // Soft Green
    Color(0xFF80DEEA), // Soft Cyan
    Color(0xFFF48FB1), // Soft Pink
    Color(0xFFFFCC80), // Soft Orange
    Color(0xFFCE93D8), // Soft Purple
    Color(0xFFE0E0E0), // Light Gray
    Color(0xFFFFCDD2), // Light Red
    Color(0xFFC8E6C9), // Light Mint
    Color(0xFFBBDEFB), // Light Blue
)

@Composable
fun RichTextToolbar(
    richTextState: RichTextState,
    modifier: Modifier = Modifier
) {
    var showTextColorPicker by remember { mutableStateOf(false) }
    var showBgColorPicker by remember { mutableStateOf(false) }

    val currentSpanStyle = richTextState.currentSpanStyle
    val currentParagraphStyle = richTextState.currentParagraphStyle

    val isBold = currentSpanStyle.fontWeight == FontWeight.Bold
    val isItalic = currentSpanStyle.fontStyle == FontStyle.Italic
    val isUnderline = currentSpanStyle.textDecoration?.contains(TextDecoration.Underline) == true

    val textAlign = currentParagraphStyle.textAlign
    val isLeft = (textAlign == TextAlign.Left) || (textAlign == TextAlign.Start)
    val isCenter = textAlign == TextAlign.Center
    val isRight = (textAlign == TextAlign.Right) || (textAlign == TextAlign.End)

    val isUnorderedList = richTextState.isUnorderedList
    val isOrderedList = richTextState.isOrderedList

    val currentColor = currentSpanStyle.color
    val currentBgColor = currentSpanStyle.background

    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // --- BOLD ---
        IconButton(
            onClick = {
                richTextState.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold))
            },
            modifier = Modifier.background(
                if (isBold) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                shape = CircleShape
            )
        ) {
            Icon(
                Icons.Default.FormatBold,
                contentDescription = "Жирный",
                tint = if (isBold) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }

        // --- ITALIC ---
        IconButton(
            onClick = {
                richTextState.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic))
            },
            modifier = Modifier.background(
                if (isItalic) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                shape = CircleShape
            )
        ) {
            Icon(
                Icons.Default.FormatItalic,
                contentDescription = "Курсив",
                tint = if (isItalic) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }

        // --- UNDERLINE ---
        IconButton(
            onClick = {
                richTextState.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline))
            },
            modifier = Modifier.background(
                if (isUnderline) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                shape = CircleShape
            )
        ) {
            Icon(
                Icons.Default.FormatUnderlined,
                contentDescription = "Подчёркнутый",
                tint = if (isUnderline) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }

        VerticalDivider(
            modifier = Modifier
                .height(24.dp)
                .padding(horizontal = 4.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // --- ALIGNMENT: LEFT ---
        IconButton(
            onClick = {
                richTextState.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.Left))
            },
            modifier = Modifier.background(
                if (isLeft) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                shape = CircleShape
            )
        ) {
            Icon(
                Icons.AutoMirrored.Filled.FormatAlignLeft,
                contentDescription = "По левому краю",
                tint = if (isLeft) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }

        // --- ALIGNMENT: CENTER ---
        IconButton(
            onClick = {
                richTextState.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.Center))
            },
            modifier = Modifier.background(
                if (isCenter) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                shape = CircleShape
            )
        ) {
            Icon(
                Icons.Default.FormatAlignCenter,
                contentDescription = "По центру",
                tint = if (isCenter) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }

        // --- ALIGNMENT: RIGHT ---
        IconButton(
            onClick = {
                richTextState.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.Right))
            },
            modifier = Modifier.background(
                if (isRight) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                shape = CircleShape
            )
        ) {
            Icon(
                Icons.AutoMirrored.Filled.FormatAlignRight,
                contentDescription = "По правому краю",
                tint = if (isRight) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }

        VerticalDivider(
            modifier = Modifier
                .height(24.dp)
                .padding(horizontal = 4.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // --- BULLET LIST ---
        IconButton(
            onClick = {
                richTextState.toggleUnorderedList()
            },
            modifier = Modifier.background(
                if (isUnorderedList) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                shape = CircleShape
            )
        ) {
            Icon(
                Icons.AutoMirrored.Filled.FormatListBulleted,
                contentDescription = "Маркированный список",
                tint = if (isUnorderedList) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }

        // --- NUMBERED LIST ---
        IconButton(
            onClick = {
                richTextState.toggleOrderedList()
            },
            modifier = Modifier.background(
                if (isOrderedList) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                shape = CircleShape
            )
        ) {
            Icon(
                Icons.Default.FormatListNumbered,
                contentDescription = "Нумерованный список",
                tint = if (isOrderedList) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }

        VerticalDivider(
            modifier = Modifier
                .height(24.dp)
                .padding(horizontal = 4.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // --- TEXT COLOR ---
        Box {
            IconButton(onClick = { showTextColorPicker = true }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.FormatColorText,
                        contentDescription = "Цвет текста",
                        tint = if (currentColor.isSpecified && currentColor != Color.Unspecified) currentColor else MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .height(3.dp)
                            .background(
                                if (currentColor.isSpecified && currentColor != Color.Unspecified) currentColor else MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(1.dp)
                            )
                    )
                }
            }

            DropdownMenu(
                expanded = showTextColorPicker,
                onDismissRequest = { showTextColorPicker = false }
            ) {
                Text(
                    text = "Цвет текста",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                ColorPaletteGrid(
                    colors = textColors,
                    selectedColor = currentColor,
                    onColorSelected = { color ->
                        if (color == null) {
                            richTextState.removeSpanStyle(SpanStyle(color = richTextState.currentSpanStyle.color))
                        } else {
                            richTextState.addSpanStyle(SpanStyle(color = color))
                        }
                        showTextColorPicker = false
                    }
                )
            }
        }

        // --- BACKGROUND / HIGHLIGHT COLOR ---
        Box {
            IconButton(onClick = { showBgColorPicker = true }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.FormatColorFill,
                        contentDescription = "Цвет фона текста",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .height(3.dp)
                            .background(
                                if (currentBgColor.isSpecified && currentBgColor != Color.Unspecified) currentBgColor else Color.Transparent,
                                shape = RoundedCornerShape(1.dp)
                            )
                    )
                }
            }

            DropdownMenu(
                expanded = showBgColorPicker,
                onDismissRequest = { showBgColorPicker = false }
            ) {
                Text(
                    text = "Цвет фона текста",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                ColorPaletteGrid(
                    colors = bgHighlightColors,
                    selectedColor = currentBgColor,
                    onColorSelected = { color ->
                        if (color == null) {
                            richTextState.removeSpanStyle(SpanStyle(background = richTextState.currentSpanStyle.background))
                        } else {
                            richTextState.addSpanStyle(SpanStyle(background = color))
                        }
                        showBgColorPicker = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ColorPaletteGrid(
    colors: List<Color>,
    selectedColor: Color,
    onColorSelected: (Color?) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { onColorSelected(null) }
                .padding(vertical = 6.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FormatColorReset,
                    contentDescription = "Сбросить цвет",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "Сбросить цвет",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        val chunkedColors = colors.chunked(5)
        for (row in chunkedColors) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (color in row) {
                    val isSelected = selectedColor.isSpecified && selectedColor == color
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                            .clickable { onColorSelected(color) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (color.luminance() < 0.5f) Color.White else Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
