package com.trodar.domain.repository

interface BackupRepository {
    suspend fun exportBackup(): ByteArray
    suspend fun importBackup(bytes: ByteArray): Result<Unit>
}
