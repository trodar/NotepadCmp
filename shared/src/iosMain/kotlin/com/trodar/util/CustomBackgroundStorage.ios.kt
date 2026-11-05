package com.trodar.util

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSDate
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.timeIntervalSince1970
import platform.Foundation.writeToFile

class IosCustomBackgroundStorage : CustomBackgroundStorage {

    @OptIn(ExperimentalForeignApi::class)
    private fun getDirectoryPath(themeName: String): String {
        val fileManager = NSFileManager.defaultManager
        val documentDirectory: NSURL? = fileManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null
        )
        val basePath = requireNotNull(documentDirectory?.path) + "/app/background/$themeName"
        fileManager.createDirectoryAtPath(basePath, withIntermediateDirectories = true, attributes = null, error = null)
        return basePath
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    override suspend fun saveCustomBackground(themeName: String, bytes: ByteArray): String = withContext(Dispatchers.IO) {
        val dirPath = getDirectoryPath(themeName)
        val timestamp = (NSDate().timeIntervalSince1970 * 1000).toLong()
        val fileName = "custom_${timestamp}_${(1000..9999).random()}.png"
        val filePath = "$dirPath/$fileName"
        val data = bytes.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = bytes.size.toULong())
        }
        data.writeToFile(filePath, atomically = true)
        fileName
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    override suspend fun getCustomBackgrounds(themeName: String): List<Pair<String, ByteArray>> = withContext(Dispatchers.IO) {
        val dirPath = getDirectoryPath(themeName)
        val fileManager = NSFileManager.defaultManager
        val contents = fileManager.contentsOfDirectoryAtPath(dirPath, error = null) as? List<*> ?: return@withContext emptyList()
        contents.mapNotNull { nameObj ->
            val name = nameObj as? String ?: return@mapNotNull null
            if (!name.startsWith("custom_")) return@mapNotNull null
            val filePath = "$dirPath/$name"
            val data = NSData.dataWithContentsOfFile(filePath) ?: return@mapNotNull null
            val bytes = data.bytes?.readBytes(data.length.toInt()) ?: return@mapNotNull null
            name to bytes
        }.sortedBy { it.first }
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    override suspend fun getCustomBackground(themeName: String, fileName: String): ByteArray? = withContext(Dispatchers.IO) {
        val dirPath = getDirectoryPath(themeName)
        val filePath = "$dirPath/$fileName"
        if (!NSFileManager.defaultManager.fileExistsAtPath(filePath)) return@withContext null
        val data = NSData.dataWithContentsOfFile(filePath) ?: return@withContext null
        data.bytes?.readBytes(data.length.toInt())
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun deleteCustomBackground(themeName: String, fileName: String): Unit = withContext(Dispatchers.IO) {
        val dirPath = getDirectoryPath(themeName)
        val filePath = "$dirPath/$fileName"
        val fileManager = NSFileManager.defaultManager
        if (fileManager.fileExistsAtPath(filePath)) {
            fileManager.removeItemAtPath(filePath, error = null)
        }
    }
}
