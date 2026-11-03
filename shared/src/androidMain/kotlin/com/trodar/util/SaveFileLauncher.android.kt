package com.trodar.util

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
actual fun rememberSaveFileLauncher(
    onResult: suspend (fileUriOrPath: String?) -> Unit
): SaveFileLauncher {
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        scope.launch {
            onResult(uri?.toString())
        }
    }
    return remember(launcher) {
        object : SaveFileLauncher {
            override fun launch(suggestedFileName: String, mimeType: String) {
                launcher.launch(suggestedFileName)
            }
        }
    }
}
