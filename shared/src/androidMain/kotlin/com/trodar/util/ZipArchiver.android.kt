package com.trodar.util

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

actual fun zipEntries(entries: List<ZipEntryData>): ByteArray {
    val baos = ByteArrayOutputStream()
    ZipOutputStream(BufferedOutputStream(baos)).use { zipOut ->
        for (entry in entries) {
            val zipEntry = ZipEntry(entry.name)
            zipOut.putNextEntry(zipEntry)
            zipOut.write(entry.bytes)
            zipOut.closeEntry()
        }
    }
    return baos.toByteArray()
}

actual fun unzipEntries(bytes: ByteArray): List<ZipEntryData> {
    val result = mutableListOf<ZipEntryData>()
    try {
        ZipInputStream(BufferedInputStream(ByteArrayInputStream(bytes))).use { zipIn ->
            var entry = zipIn.nextEntry
            while (entry != null) {
                if (!entry.isDirectory) {
                    val content = zipIn.readBytes()
                    result.add(ZipEntryData(entry.name, content))
                }
                zipIn.closeEntry()
                entry = zipIn.nextEntry
            }
        }
    } catch (_: Exception) {
    }
    return result
}
