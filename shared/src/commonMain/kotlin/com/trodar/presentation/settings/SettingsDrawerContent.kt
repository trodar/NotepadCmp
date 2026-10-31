package com.trodar.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trodar.domain.model.AppTheme
import com.trodar.presentation.theme.TextThemeType
import com.trodar.presentation.theme.ThemedIcon
import com.trodar.presentation.theme.ThemedText

@Composable
fun SettingsDrawerContent(
    currentTheme: AppTheme,
    onSelectTheme: (AppTheme) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ThemedIcon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            ThemedText(
                text = "Настройки",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                type = TextThemeType.PRIMARY
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        // Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ThemedIcon(
                imageVector = Icons.Default.Palette,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            ThemedText(
                text = "Тема оформления",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                type = TextThemeType.PRIMARY
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Theme Circles Grid (2 rows of 3 circles, no text)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
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
                            AppTheme.LIGHT -> Color(0xFF2E7D32)
                            AppTheme.DARK -> Color(0xFF212121)
                            AppTheme.PINK -> Color(0xFFD81B60)
                            AppTheme.PURPLE -> Color(0xFF673AB7)
                            AppTheme.BLUE -> Color(0xFF0288D1)
                            AppTheme.YELLOW -> Color(0xFFFBC02D)
                        }

                        Box(
                            modifier = Modifier
                                .size(56.dp)
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
    }
}
