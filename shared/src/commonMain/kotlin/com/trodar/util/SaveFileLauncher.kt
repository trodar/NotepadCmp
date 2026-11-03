package com.trodar.util

import androidx.compose.runtime.Composable

interface SaveFileLauncher {
    fun launch(suggestedFileName: String, mimeType: String = "application/zip")
}

@Composable
expect fun rememberSaveFileLauncher(
    onResult: suspend (fileUriOrPath: String?) -> Unit
): SaveFileLauncher
