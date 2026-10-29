package com.trodar.data.repository

import androidx.datastore.core.DataStore
import com.trodar.data.datastore.AppThemeProto
import com.trodar.data.datastore.UserPreferences
import com.trodar.domain.model.AppTheme
import com.trodar.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val dataStore: DataStore<UserPreferences>
) : SettingsRepository {

    override val appTheme: Flow<AppTheme> = dataStore.data.map { prefs ->
        when (prefs.theme) {
            AppThemeProto.THEME_LIGHT -> AppTheme.LIGHT
            AppThemeProto.THEME_DARK -> AppTheme.DARK
            AppThemeProto.THEME_PINK -> AppTheme.PINK
            AppThemeProto.THEME_PURPLE -> AppTheme.PURPLE
            AppThemeProto.THEME_BLUE -> AppTheme.BLUE
        }
    }

    override suspend fun setAppTheme(theme: AppTheme) {
        val protoTheme = when (theme) {
            AppTheme.LIGHT -> AppThemeProto.THEME_LIGHT
            AppTheme.DARK -> AppThemeProto.THEME_DARK
            AppTheme.PINK -> AppThemeProto.THEME_PINK
            AppTheme.PURPLE -> AppThemeProto.THEME_PURPLE
            AppTheme.BLUE -> AppThemeProto.THEME_BLUE
        }
        dataStore.updateData { currentPrefs ->
            currentPrefs.copy(theme = protoTheme)
        }
    }
}
