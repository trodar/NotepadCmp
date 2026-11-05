package com.trodar.domain.repository

import com.trodar.domain.model.AppTheme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val appTheme: Flow<AppTheme>
    val selectedBackgroundId: Flow<String>
    val backgroundImageIndex: Flow<Int>
    suspend fun setAppTheme(theme: AppTheme)
    suspend fun setSelectedBackgroundId(id: String)
    suspend fun setBackgroundImageIndex(index: Int)
}
