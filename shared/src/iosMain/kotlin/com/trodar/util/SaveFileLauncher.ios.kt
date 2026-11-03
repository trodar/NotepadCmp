package com.trodar.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIWindow
import platform.UniformTypeIdentifiers.UTTypeFolder
import platform.darwin.NSObject

@Composable
actual fun rememberSaveFileLauncher(
    onResult: suspend (fileUriOrPath: String?) -> Unit
): SaveFileLauncher {
    val scope = rememberCoroutineScope()
    return remember(scope) {
        object : SaveFileLauncher {
            private var activeDelegate: SaveLocationPickerDelegate? = null

            override fun launch(suggestedFileName: String, mimeType: String) {
                val keyWindow = UIApplication.sharedApplication.keyWindow
                    ?: (UIApplication.sharedApplication.windows.firstOrNull() as? UIWindow)
                val rootViewController = keyWindow?.rootViewController
                if (rootViewController == null) {
                    scope.launch { onResult(null) }
                    return
                }

                val picker = UIDocumentPickerViewController(
                    forOpeningContentTypes = listOf(UTTypeFolder),
                    asCopy = false,
                )
                picker.allowsMultipleSelection = false
                val delegate = SaveLocationPickerDelegate(
                    suggestedFileName = suggestedFileName,
                    scope = scope,
                    onResult = onResult,
                    onFinished = { activeDelegate = null },
                )
                activeDelegate = delegate
                picker.delegate = delegate
                rootViewController.presentViewController(picker, animated = true, completion = null)
            }
        }
    }
}

private class SaveLocationPickerDelegate(
    private val suggestedFileName: String,
    private val scope: CoroutineScope,
    private val onResult: suspend (String?) -> Unit,
    private val onFinished: () -> Unit,
) : NSObject(), UIDocumentPickerDelegateProtocol {
    override fun documentPicker(
        controller: UIDocumentPickerViewController,
        didPickDocumentsAtURLs: List<*>,
    ) {
        val folderUrl = didPickDocumentsAtURLs.firstOrNull() as? NSURL
        if (folderUrl == null) {
            scope.launch {
                try {
                    onResult(null)
                } finally {
                    onFinished()
                }
            }
            return
        }

        val hasScopedAccess = folderUrl.startAccessingSecurityScopedResource()
        val destination = folderUrl.URLByAppendingPathComponent(suggestedFileName)?.path
        scope.launch {
            try {
                onResult(destination)
            } finally {
                if (hasScopedAccess) folderUrl.stopAccessingSecurityScopedResource()
                onFinished()
            }
        }
    }

    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
        scope.launch {
            try {
                onResult(null)
            } finally {
                onFinished()
            }
        }
    }
}
