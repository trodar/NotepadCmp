package com.trodar.util

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
actual fun rememberOpenFileLauncher(
    onResult: suspend (bytes: ByteArray?) -> Unit
): OpenFileLauncher {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) {
            scope.launch { onResult(null) }
        } else {
            scope.launch(Dispatchers.IO) {
                val bytes = try {
                    context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                } catch (_: Exception) {
                    null
                }
                withContext(Dispatchers.Main) {
                    onResult(bytes)
                }
            }
        }
    }
    return remember(launcher) {
        object : OpenFileLauncher {
            override fun launch() {
                launcher.launch(arrayOf("*/*"))
            }
        }
    }
}
