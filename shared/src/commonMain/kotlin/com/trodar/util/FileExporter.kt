package com.trodar.util

interface FileExporter {
    suspend fun saveBackupToDestination(destinationUriOrPath: String, bytes: ByteArray): String
}
