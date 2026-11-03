package com.trodar.util

import android.content.Context
import android.net.Uri
import java.io.File

class AndroidFileExporter(
    private val context: Context
) : FileExporter {
    override suspend fun saveBackupToDestination(destinationUriOrPath: String, bytes: ByteArray): String {
        return if (destinationUriOrPath.startsWith("content://")) {
            val uri = Uri.parse(destinationUriOrPath)
            context.contentResolver.openOutputStream(uri, "wt")?.use { outputStream ->
                outputStream.write(bytes)
            } ?: error("Could not open output stream for $destinationUriOrPath")
            destinationUriOrPath
        } else {
            val file = File(destinationUriOrPath)
            file.parentFile?.mkdirs()
            file.writeBytes(bytes)
            file.absolutePath
        }
    }
}
