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
        prefs.theme.toAppTheme()
    }

    override val selectedBackgroundId: Flow<String> = dataStore.data.map { prefs ->
        prefs.getBackgroundIdForTheme(prefs.theme)
    }

    override val backgroundImageIndex: Flow<Int> = dataStore.data.map { prefs ->
        val id = prefs.getBackgroundIdForTheme(prefs.theme)
        id.toIntOrNull() ?: 0
    }

    override suspend fun setAppTheme(theme: AppTheme) {
        val protoTheme = theme.toProto()
        dataStore.updateData { currentPrefs ->
            currentPrefs.copy(theme = protoTheme)
        }
    }

    override suspend fun setSelectedBackgroundId(id: String) {
        dataStore.updateData { currentPrefs ->
            currentPrefs.setBackgroundIdForTheme(currentPrefs.theme, id)
        }
    }

    override suspend fun setBackgroundImageIndex(index: Int) {
        setSelectedBackgroundId(index.toString())
    }
}

private fun AppThemeProto.toAppTheme(): AppTheme = when (this) {
    AppThemeProto.THEME_LIGHT -> AppTheme.GREEN
    AppThemeProto.THEME_DARK -> AppTheme.DARK
    AppThemeProto.THEME_PINK -> AppTheme.PINK
    AppThemeProto.THEME_PURPLE -> AppTheme.PURPLE
    AppThemeProto.THEME_BLUE -> AppTheme.BLUE
    AppThemeProto.THEME_YELLOW -> AppTheme.YELLOW
}

private fun AppTheme.toProto(): AppThemeProto = when (this) {
    AppTheme.GREEN -> AppThemeProto.THEME_LIGHT
    AppTheme.DARK -> AppThemeProto.THEME_DARK
    AppTheme.PINK -> AppThemeProto.THEME_PINK
    AppTheme.PURPLE -> AppThemeProto.THEME_PURPLE
    AppTheme.BLUE -> AppThemeProto.THEME_BLUE
    AppTheme.YELLOW -> AppThemeProto.THEME_YELLOW
}
