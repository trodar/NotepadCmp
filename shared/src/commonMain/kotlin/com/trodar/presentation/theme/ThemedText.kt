package com.trodar.presentation.theme

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

enum class TextThemeType {
    PRIMARY,
    SECONDARY,
    ON_BACKGROUND,
    ON_SURFACE,
    ON_PRIMARY_CONTAINER,
    ERROR,
    DEFAULT
}

@Composable
fun textTint(type: TextThemeType = TextThemeType.DEFAULT): Color = when (type) {
    TextThemeType.PRIMARY -> MaterialTheme.colorScheme.primary
    TextThemeType.SECONDARY -> MaterialTheme.colorScheme.secondary
    TextThemeType.ON_BACKGROUND -> MaterialTheme.colorScheme.onBackground
    TextThemeType.ON_SURFACE -> MaterialTheme.colorScheme.onSurface
    TextThemeType.ON_PRIMARY_CONTAINER -> MaterialTheme.colorScheme.onPrimaryContainer
    TextThemeType.ERROR -> MaterialTheme.colorScheme.error
    TextThemeType.DEFAULT -> Color.Unspecified
}

@Composable
fun ThemedText(
    text: String,
    modifier: Modifier = Modifier,
    type: TextThemeType = TextThemeType.DEFAULT,
    style: TextStyle = LocalTextStyle.current,
    fontWeight: FontWeight? = null,
    fontStyle: FontStyle? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {}
) {
    Text(
        text = text,
        modifier = modifier,
        color = textTint(type),
        style = style,
        fontWeight = fontWeight,
        fontStyle = fontStyle,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout
    )
}
