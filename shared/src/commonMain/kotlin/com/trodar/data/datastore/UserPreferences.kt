package com.trodar.data.datastore

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoNumber
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@Serializable
enum class AppThemeProto {
    @ProtoNumber(0)
    THEME_LIGHT,
    @ProtoNumber(1)
    THEME_DARK,
    @ProtoNumber(2)
    THEME_PINK,
    @ProtoNumber(3)
    THEME_PURPLE,
    @ProtoNumber(4)
    THEME_BLUE,
    @ProtoNumber(5)
    THEME_YELLOW
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class UserPreferences(
    @ProtoNumber(1)
    val theme: AppThemeProto = AppThemeProto.THEME_LIGHT,
    @ProtoNumber(2)
    val backgroundIndex: Int = 0,
    @ProtoNumber(3)
    val themeBackgrounds: Map<AppThemeProto, Int> = emptyMap(),
    @ProtoNumber(4)
    val themeBackgroundIds: Map<AppThemeProto, String> = emptyMap()
) {
    fun getBackgroundIdForTheme(themeProto: AppThemeProto): String {
        return themeBackgroundIds[themeProto]
            ?: themeBackgrounds[themeProto]?.toString()
            ?: backgroundIndex.toString()
    }

    fun setBackgroundIdForTheme(themeProto: AppThemeProto, id: String): UserPreferences {
        val intVal = id.toIntOrNull() ?: 0
        return copy(
            backgroundIndex = intVal,
            themeBackgrounds = themeBackgrounds + (themeProto to intVal),
            themeBackgroundIds = themeBackgroundIds + (themeProto to id)
        )
    }
}
