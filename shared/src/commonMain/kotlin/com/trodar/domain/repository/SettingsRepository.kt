package com.trodar.domain.repository

import com.trodar.domain.model.AppTheme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val appTheme: Flow<AppTheme>
    suspend fun setAppTheme(theme: AppTheme)
}
