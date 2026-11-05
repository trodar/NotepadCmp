package com.trodar.util

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class AndroidCustomBackgroundStorage(
    private val context: Context
) : CustomBackgroundStorage {

    override suspend fun saveCustomBackground(themeName: String, bytes: ByteArray): String = withContext(Dispatchers.IO) {
        val dir = File(context.filesDir, "app/background/$themeName")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val fileName = "custom_${System.currentTimeMillis()}_${(1000..9999).random()}.png"
        val file = File(dir, fileName)
        file.writeBytes(bytes)
        fileName
    }

    override suspend fun getCustomBackgrounds(themeName: String): List<Pair<String, ByteArray>> = withContext(Dispatchers.IO) {
        val dir = File(context.filesDir, "app/background/$themeName")
        if (!dir.exists()) return@withContext emptyList()
        dir.listFiles()
            ?.filter { it.isFile && it.name.startsWith("custom_") }
            ?.sortedBy { it.name }
            ?.map { file -> file.name to file.readBytes() }
            ?: emptyList()
    }

    override suspend fun getCustomBackground(themeName: String, fileName: String): ByteArray? = withContext(Dispatchers.IO) {
        val file = File(context.filesDir, "app/background/$themeName/$fileName")
        if (file.exists()) file.readBytes() else null
    }

    override suspend fun deleteCustomBackground(themeName: String, fileName: String): Unit = withContext(Dispatchers.IO) {
        val file = File(context.filesDir, "app/background/$themeName/$fileName")
        if (file.exists()) {
            file.delete()
        }
    }
}
