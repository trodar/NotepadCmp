package com.trodar.util

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
import platform.Foundation.writeToFile

class IosFileExporter : FileExporter {
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    override suspend fun saveBackupToDestination(destinationUriOrPath: String, bytes: ByteArray): String {
        val nsData = bytes.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = bytes.size.toULong())
        }
        nsData.writeToFile(destinationUriOrPath, atomically = true)
        return destinationUriOrPath
    }
}
