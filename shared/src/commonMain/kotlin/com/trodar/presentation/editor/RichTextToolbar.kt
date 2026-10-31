package com.trodar.presentation.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kborowy.colorpicker.KolorPicker
import com.kborowy.colorpicker.config.PickerConfig
import com.kborowy.colorpicker.config.TrackConfig
import com.mohamedrejeb.richeditor.model.RichTextState

@Composable
fun RichTextToolbar(
    richTextState: RichTextState,
    modifier: Modifier = Modifier,
    noteBgColor: Color = Color.Unspecified,
    onNoteBgColorSelected: (Color?) -> Unit = {}
) {
    var showFontSizePicker by remember { mutableStateOf(false) }
    var showTextColorPicker by remember { mutableStateOf(false) }
    var showBgColorPicker by remember { mutableStateOf(false) }
    var showNoteBgColorPicker by remember { mutableStateOf(false) }

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
        // --- 1. FONT SIZE (Размер текста) ---
        Box {
            IconButton(onClick = { showFontSizePicker = true }) {
                Icon(
                    Icons.Default.FormatSize,
                    contentDescription = "Размер текста",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            DropdownMenu(
                expanded = showFontSizePicker,
                onDismissRequest = { showFontSizePicker = false }
            ) {
                Text(
                    text = "Размер текста",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
                HorizontalDivider()

                val fontSizes = listOf(
                    12.sp to "12 sp (Мелкий)",
                    14.sp to "14 sp (Небольшой)",
                    16.sp to "16 sp (Стандартный)",
                    18.sp to "18 sp (Средний)",
                    20.sp to "20 sp (Крупный)",
                    24.sp to "24 sp (Подзаголовок)",
                    28.sp to "28 sp (Заголовок)",
                    32.sp to "32 sp (Большой)"
                )

                for ((size, label) in fontSizes) {
                    val isSelected = currentSpanStyle.fontSize != TextUnit.Unspecified && currentSpanStyle.fontSize == size
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        trailingIcon = if (isSelected) {
                            {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null,
                        onClick = {
                            richTextState.addSpanStyle(SpanStyle(fontSize = size))
                            showFontSizePicker = false
                        }
                    )
                }
            }
        }

        VerticalDivider(
            modifier = Modifier
                .height(24.dp)
                .padding(horizontal = 4.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

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
        IconButton(onClick = { showTextColorPicker = true }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.FormatColorText,
                    contentDescription = "Цвет текста",
                    tint = MaterialTheme.colorScheme.onSurface
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

        // --- BACKGROUND / HIGHLIGHT COLOR ---
        IconButton(onClick = { showBgColorPicker = true }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.BorderColor,
                    contentDescription = "Маркер текста",
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

        VerticalDivider(
            modifier = Modifier
                .height(24.dp)
                .padding(horizontal = 4.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // --- NOTE PAGE BACKGROUND COLOR ---
        IconButton(onClick = { showNoteBgColorPicker = true }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Palette,
                    contentDescription = "Фон всей заметки",
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Box(
                    modifier = Modifier
                        .width(18.dp)
                        .height(3.dp)
                        .background(
                            if (noteBgColor.isSpecified && noteBgColor != Color.Unspecified) noteBgColor else Color.Transparent,
                            shape = RoundedCornerShape(1.dp)
                        )
                )
            }
        }
    }

    if (showTextColorPicker) {
        ColorPickerDialog(
            title = "Цвет текста",
            initialColor = currentColor,
            onDismissRequest = { showTextColorPicker = false },
            onResetColor = {
                richTextState.removeSpanStyle(SpanStyle(color = richTextState.currentSpanStyle.color))
            },
            onColorSelected = { selected ->
                richTextState.addSpanStyle(SpanStyle(color = selected))
            }
        )
    }

    if (showBgColorPicker) {
        ColorPickerDialog(
            title = "Маркер (фон текста)",
            initialColor = currentBgColor,
            onDismissRequest = { showBgColorPicker = false },
            onResetColor = {
                richTextState.removeSpanStyle(SpanStyle(background = richTextState.currentSpanStyle.background))
            },
            onColorSelected = { selected ->
                richTextState.addSpanStyle(SpanStyle(background = selected))
            }
        )
    }

    if (showNoteBgColorPicker) {
        ColorPickerDialog(
            title = "Фон всей заметки",
            initialColor = noteBgColor,
            onDismissRequest = { showNoteBgColorPicker = false },
            onResetColor = {
                onNoteBgColorSelected(null)
            },
            onColorSelected = { selected ->
                onNoteBgColorSelected(selected)
            }
        )
    }
}

@Composable
private fun ColorPickerDialog(
    title: String,
    initialColor: Color,
    onDismissRequest: () -> Unit,
    onResetColor: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    var selectedColor by remember(initialColor) {
        mutableStateOf(if (initialColor.isSpecified && initialColor != Color.Unspecified) initialColor else Color.Black)
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(selectedColor)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    KolorPicker(
                        initialColor = selectedColor,
                        onColorSelected = { color ->
                            selectedColor = color
                        },
                        pickerConfig = PickerConfig.Default,
                        alphaTrackConfig = TrackConfig.Default,
                        hueTrackConfig = TrackConfig.Default,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onColorSelected(selectedColor)
                onDismissRequest()
            }) {
                Text("Выбрать")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {
                    onResetColor()
                    onDismissRequest()
                }) {
                    Text("Сбросить")
                }
                TextButton(onClick = onDismissRequest) {
                    Text("Отмена")
                }
            }
        }
    )
}
