package com.trodar.util

data class ZipEntryData(
    val name: String,
    val bytes: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ZipEntryData) return false
        return name == other.name && bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int = 31 * name.hashCode() + bytes.contentHashCode()
}

expect fun zipEntries(entries: List<ZipEntryData>): ByteArray
expect fun unzipEntries(bytes: ByteArray): List<ZipEntryData>
