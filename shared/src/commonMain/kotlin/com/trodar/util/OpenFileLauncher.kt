package com.trodar.util

import androidx.compose.runtime.Composable

interface OpenFileLauncher {
    fun launch()
}

@Composable
expect fun rememberOpenFileLauncher(
    onResult: suspend (bytes: ByteArray?) -> Unit
): OpenFileLauncher
