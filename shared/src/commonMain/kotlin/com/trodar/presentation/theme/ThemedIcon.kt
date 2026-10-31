package com.trodar.presentation.theme

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class IconThemeType {
    PRIMARY,
    SECONDARY,
    ERROR,
    DEFAULT
}

@Composable
fun iconTint(type: IconThemeType = IconThemeType.PRIMARY): Color = when (type) {
    IconThemeType.PRIMARY -> MaterialTheme.colorScheme.primary
    IconThemeType.SECONDARY -> MaterialTheme.colorScheme.secondary
    IconThemeType.ERROR -> MaterialTheme.colorScheme.error
    IconThemeType.DEFAULT -> LocalContentColor.current
}

@Composable
fun ThemedIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    type: IconThemeType = IconThemeType.PRIMARY
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = iconTint(type)
    )
}
