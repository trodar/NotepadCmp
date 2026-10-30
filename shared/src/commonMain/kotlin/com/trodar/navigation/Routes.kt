package com.trodar.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute

@Serializable
sealed interface TopLevelRoute : AppRoute

@Serializable
@SerialName("NoteList")
data object NoteListRoute : AppRoute, TopLevelRoute

@Serializable
@SerialName("NoteEditor")
data class NoteEditorRoute(
    val noteId: Long,
    val folderId: Long? = null
) : AppRoute
