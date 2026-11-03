package com.trodar.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIWindow
import platform.UniformTypeIdentifiers.UTTypeItem
import platform.darwin.NSObject

@Composable
actual fun rememberOpenFileLauncher(
    onResult: suspend (bytes: ByteArray?) -> Unit
): OpenFileLauncher {
    val scope = rememberCoroutineScope()
    return remember(scope) {
        object : OpenFileLauncher {
            private var activeDelegate: OpenFilePickerDelegate? = null

            override fun launch() {
                val keyWindow = UIApplication.sharedApplication.keyWindow
                    ?: (UIApplication.sharedApplication.windows.firstOrNull() as? UIWindow)
                val rootViewController = keyWindow?.rootViewController
                if (rootViewController == null) {
                    scope.launch { onResult(null) }
                    return
                }

                val picker = UIDocumentPickerViewController(
                    forOpeningContentTypes = listOf(UTTypeItem),
                    asCopy = true,
                )
                picker.allowsMultipleSelection = false
                val delegate = OpenFilePickerDelegate(
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

private class OpenFilePickerDelegate(
    private val scope: CoroutineScope,
    private val onResult: suspend (ByteArray?) -> Unit,
    private val onFinished: () -> Unit,
) : NSObject(), UIDocumentPickerDelegateProtocol {
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    override fun documentPicker(
        controller: UIDocumentPickerViewController,
        didPickDocumentsAtURLs: List<*>,
    ) {
        val fileUrl = didPickDocumentsAtURLs.firstOrNull() as? NSURL
        if (fileUrl == null) {
            scope.launch {
                try {
                    onResult(null)
                } finally {
                    onFinished()
                }
            }
            return
        }

        val hasScopedAccess = fileUrl.startAccessingSecurityScopedResource()
        val data = NSData.dataWithContentsOfURL(fileUrl)
        val bytes = data?.let { nsData ->
            nsData.bytes?.let { ptr ->
                ptr.readBytes(nsData.length.toInt())
            }
        }

        scope.launch {
            try {
                onResult(bytes)
            } finally {
                if (hasScopedAccess) fileUrl.stopAccessingSecurityScopedResource()
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
