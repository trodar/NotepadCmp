package com.trodar.util

actual fun zipEntries(entries: List<ZipEntryData>): ByteArray {
    val baos = mutableListOf<Byte>()
    for (entry in entries) {
        val nameBytes = entry.name.encodeToByteArray()
        baos.addAll(intToBytes(nameBytes.size).toList())
        baos.addAll(nameBytes.toList())
        baos.addAll(intToBytes(entry.bytes.size).toList())
        baos.addAll(entry.bytes.toList())
    }
    return baos.toByteArray()
}

actual fun unzipEntries(bytes: ByteArray): List<ZipEntryData> {
    val result = mutableListOf<ZipEntryData>()
    var offset = 0
    while (offset < bytes.size) {
        if (offset + 4 > bytes.size) break
        val nameLen = bytesToInt(bytes, offset)
        offset += 4
        if (offset + nameLen > bytes.size) break
        val name = bytes.decodeToString(offset, offset + nameLen)
        offset += nameLen
        if (offset + 4 > bytes.size) break
        val dataLen = bytesToInt(bytes, offset)
        offset += 4
        if (offset + dataLen > bytes.size) break
        val data = bytes.copyOfRange(offset, offset + dataLen)
        offset += dataLen
        result.add(ZipEntryData(name, data))
    }
    return result
}

private fun intToBytes(value: Int): ByteArray = byteArrayOf(
    (value shr 24).toByte(),
    (value shr 16).toByte(),
    (value shr 8).toByte(),
    value.toByte()
)

private fun bytesToInt(bytes: ByteArray, offset: Int): Int =
    ((bytes[offset].toInt() and 0xFF) shl 24) or
    ((bytes[offset + 1].toInt() and 0xFF) shl 16) or
    ((bytes[offset + 2].toInt() and 0xFF) shl 8) or
    (bytes[offset + 3].toInt() and 0xFF)
