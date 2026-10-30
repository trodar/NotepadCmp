package com.trodar.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds

@Composable
internal fun NavScaffold(
    navState: NavState,
    navigator: Navigator,
    content: @Composable (() -> Unit)
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
    ) {
        content()
    }
}
