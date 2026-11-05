package com.trodar.presentation.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image as ImageIcon
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trodar.domain.model.AppTheme
import com.trodar.domain.model.getBackgroundResource
import com.trodar.util.toImageBitmap
import notead.shared.generated.resources.Res
import notead.shared.generated.resources.Settings
import notead.shared.generated.resources.download
import notead.shared.generated.resources.upload
import org.jetbrains.compose.resources.painterResource

sealed interface BackgroundOption {
    val id: String

    data object None : BackgroundOption {
        override val id: String = "0"
    }

    data class Preset(val index: Int) : BackgroundOption {
        override val id: String = index.toString()
    }

    data class Custom(val fileName: String, val bytes: ByteArray) : BackgroundOption {
        override val id: String = fileName
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Custom) return false
            if (fileName != other.fileName) return false
            if (!bytes.contentEquals(other.bytes)) return false
            return true
        }
        override fun hashCode(): Int {
            var result = fileName.hashCode()
            result = 31 * result + bytes.contentHashCode()
            return result
        }
    }

    data object AddNew : BackgroundOption {
        override val id: String = "add_new"
    }
}

@Composable
fun SettingsDrawerContent(
    currentTheme: AppTheme,
    modifier: Modifier = Modifier,
    selectedBackgroundId: String = "0",
    customBackgrounds: List<Pair<String, ByteArray>> = emptyList(),
    onSelectTheme: (AppTheme) -> Unit,
    onSelectBackgroundId: (String) -> Unit = {},
    onDeleteCustomBackground: (String) -> Unit = {},
    onPickCustomBackground: () -> Unit = {},
    onExportBackup: () -> Unit = {},
    onImportBackup: () -> Unit = {}
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.Settings),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Настройки",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = onExportBackup,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.download),
                        contentDescription = "Экспорт бд и настроек",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = onImportBackup,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.upload),
                        contentDescription = "Импорт бд и настроек",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Section 1: Theme
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Тема оформления",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Theme Circles Grid (2 rows of 3 circles)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppTheme.entries.chunked(3).forEach { rowThemes ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        rowThemes.forEach { theme ->
                            val isSelected = currentTheme == theme
                            val previewColor = when (theme) {
                                AppTheme.GREEN -> Color(0xFF2E7D32)
                                AppTheme.DARK -> Color(0xFF212121)
                                AppTheme.PINK -> Color(0xFFD81B60)
                                AppTheme.PURPLE -> Color(0xFF673AB7)
                                AppTheme.BLUE -> Color(0xFF0288D1)
                                AppTheme.YELLOW -> Color(0xFFFBC02D)
                            }

                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(color = previewColor, shape = CircleShape)
                                    .border(
                                        width = if (isSelected) 3.5.dp else 2.dp,
                                        color = if (isSelected) Color(0xFFFF9800) else Color(0xFF555555),
                                        shape = CircleShape
                                    )
                                    .clip(CircleShape)
                                    .clickable { onSelectTheme(theme) }
                            )
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // Section 2: Background Selection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ImageIcon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Фон списка заметок",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = onPickCustomBackground,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Выбрать новый фон",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val bgOptions = buildList {
                add(BackgroundOption.None)
                for (i in 1..6) {
                    add(BackgroundOption.Preset(i))
                }
                for ((fileName, bytes) in customBackgrounds) {
                    add(BackgroundOption.Custom(fileName, bytes))
                }
                add(BackgroundOption.AddNew)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .fillMaxHeight()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(bgOptions.chunked(3)) { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        rowItems.forEach { option ->
                            val isSelected = selectedBackgroundId == option.id
                            val itemShape = RoundedCornerShape(10.dp)

                            Box(
                                modifier = Modifier
                                    .size(width = 68.dp, height = 90.dp)
                                    .border(
                                        width = if (isSelected) 3.5.dp else 1.5.dp,
                                        color = if (isSelected) Color(0xFFFF9800) else Color(0xFF777777),
                                        shape = itemShape
                                    )
                                    .clip(itemShape)
                                    .background(Color(0xFF2B2B2B))
                                    .clickable {
                                        when (option) {
                                            is BackgroundOption.AddNew -> onPickCustomBackground()
                                            else -> onSelectBackgroundId(option.id)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                when (option) {
                                    is BackgroundOption.None -> {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Block,
                                                contentDescription = "Без фона",
                                                tint = Color.White.copy(alpha = 0.7f),
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Без фона",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                color = Color.White.copy(alpha = 0.8f)
                                            )
                                        }
                                    }
                                    is BackgroundOption.Preset -> {
                                        val bgResource = currentTheme.getBackgroundResource(option.index)
                                        if (bgResource != null) {
                                            Image(
                                                painter = painterResource(bgResource),
                                                contentDescription = "Фон ${option.index}",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                    }
                                    is BackgroundOption.Custom -> {
                                        val bitmap = remember(option.bytes) {
                                            option.bytes.toImageBitmap()
                                        }
                                        Image(
                                            bitmap = bitmap,
                                            contentDescription = "Свой фон",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(3.dp)
                                                .size(20.dp)
                                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                                .clickable { onDeleteCustomBackground(option.fileName) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Удалить фон",
                                                tint = Color.White,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                    is BackgroundOption.AddNew -> {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Добавить фон",
                                                tint = Color.White.copy(alpha = 0.7f),
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Свой фон",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                color = Color.White.copy(alpha = 0.8f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        repeat(3 - rowItems.size) {
                            Spacer(modifier = Modifier.size(width = 68.dp, height = 90.dp))
                        }
                    }
                }
            }
        }
    }
}
