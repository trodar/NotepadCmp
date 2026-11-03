package com.trodar.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.swing.JFileChooser

@Composable
actual fun rememberOpenFileLauncher(
    onResult: suspend (bytes: ByteArray?) -> Unit
): OpenFileLauncher {
    val scope = rememberCoroutineScope()
    return remember(scope) {
        object : OpenFileLauncher {
            override fun launch() {
                scope.launch(Dispatchers.IO) {
                    val fileChooser = JFileChooser().apply {
                        dialogTitle = "Выберите файл резервной копии"
                    }
                    val userSelection = fileChooser.showOpenDialog(null)
                    val bytes = if (userSelection == JFileChooser.APPROVE_OPTION) {
                        try {
                            fileChooser.selectedFile.readBytes()
                        } catch (_: Exception) {
                            null
                        }
                    } else {
                        null
                    }
                    withContext(Dispatchers.Main) {
                        onResult(bytes)
                    }
                }
            }
        }
    }
}
