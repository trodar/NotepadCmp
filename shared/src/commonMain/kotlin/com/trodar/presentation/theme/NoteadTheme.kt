package com.trodar.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.trodar.domain.model.AppTheme

// 1. Светлый (Light / Зелёный)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8F5E9),
    onPrimaryContainer = Color(0xFF1B5E20),
    secondary = Color(0xFF388E3C),
    onSecondary = Color.White,
    background = Color(0xFFF1F8E9),
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F)
)

// 2. Темный (Dark)
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF0D47A1),
    primaryContainer = Color(0xFF1565C0),
    onPrimaryContainer = Color(0xFFE3F2FD),
    secondary = Color(0xFFB0BEC5),
    onSecondary = Color(0xFF1C313A),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE6E1E5)
)

// 3. Розовый (Pink)
private val PinkColorScheme = lightColorScheme(
    primary = Color(0xFFD81B60),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFCE4EC),
    onPrimaryContainer = Color(0xFF880E4F),
    secondary = Color(0xFFC2185B),
    onSecondary = Color.White,
    background = Color(0xFFFFF5F7),
    onBackground = Color(0xFF2C1A1D),
    surface = Color.White,
    onSurface = Color(0xFF2C1A1D)
)

// 4. Фиолетовый (Purple)
private val PurpleColorScheme = lightColorScheme(
    primary = Color(0xFF673AB7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE7F6),
    onPrimaryContainer = Color(0xFF311B92),
    secondary = Color(0xFF512DA8),
    onSecondary = Color.White,
    background = Color(0xFFFAFAFF),
    onBackground = Color(0xFF1B1B22),
    surface = Color.White,
    onSurface = Color(0xFF1B1B22)
)

// 5. Голубой (Blue)
private val BlueColorScheme = lightColorScheme(
    primary = Color(0xFF0288D1),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F7FA),
    onPrimaryContainer = Color(0xFF006064),
    secondary = Color(0xFF0097A7),
    onSecondary = Color.White,
    background = Color(0xFFF4FAFC),
    onBackground = Color(0xFF182225),
    surface = Color.White,
    onSurface = Color(0xFF182225)
)

// 6. Жёлтый (Yellow)
private val YellowColorScheme = lightColorScheme(
    primary = Color(0xFFF57F17),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF9C4),
    onPrimaryContainer = Color(0xFFF57F17),
    secondary = Color(0xFFFBC02D),
    onSecondary = Color.Black,
    background = Color(0xFFFFFDE7),
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F)
)

@Composable
fun NoteadTheme(
    appTheme: AppTheme = AppTheme.LIGHT,
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        AppTheme.LIGHT -> LightColorScheme
        AppTheme.DARK -> DarkColorScheme
        AppTheme.PINK -> PinkColorScheme
        AppTheme.PURPLE -> PurpleColorScheme
        AppTheme.BLUE -> BlueColorScheme
        AppTheme.YELLOW -> YellowColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
