package com.trodar.util

import java.io.File

class JvmFileExporter : FileExporter {
    override suspend fun saveBackupToDestination(destinationUriOrPath: String, bytes: ByteArray): String {
        val file = File(destinationUriOrPath)
        file.parentFile?.mkdirs()
        file.writeBytes(bytes)
        return file.absolutePath
    }
}
