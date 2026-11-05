package com.trodar.util

interface CustomBackgroundStorage {
    suspend fun saveCustomBackground(themeName: String, bytes: ByteArray): String
    suspend fun getCustomBackgrounds(themeName: String): List<Pair<String, ByteArray>>
    suspend fun getCustomBackground(themeName: String, fileName: String): ByteArray?
    suspend fun deleteCustomBackground(themeName: String, fileName: String)
}
