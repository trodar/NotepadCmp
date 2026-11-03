package com.trodar.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.swing.JFileChooser

@Composable
actual fun rememberSaveFileLauncher(
    onResult: suspend (fileUriOrPath: String?) -> Unit
): SaveFileLauncher {
    val scope = rememberCoroutineScope()
    return remember(scope) {
        object : SaveFileLauncher {
            override fun launch(suggestedFileName: String, mimeType: String) {
                scope.launch(Dispatchers.IO) {
                    val fileChooser = JFileChooser().apply {
                        dialogTitle = "Сохранить резервную копию"
                        selectedFile = File(suggestedFileName)
                    }
                    val userSelection = fileChooser.showSaveDialog(null)
                    val destination = if (userSelection == JFileChooser.APPROVE_OPTION) {
                        fileChooser.selectedFile.absolutePath
                    } else {
                        null
                    }
                    withContext(Dispatchers.Main) {
                        onResult(destination)
                    }
                }
            }
        }
    }
}
