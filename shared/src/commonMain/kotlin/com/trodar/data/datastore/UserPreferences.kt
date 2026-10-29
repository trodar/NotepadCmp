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
    THEME_BLUE
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class UserPreferences(
    @ProtoNumber(1)
    val theme: AppThemeProto = AppThemeProto.THEME_LIGHT
)
