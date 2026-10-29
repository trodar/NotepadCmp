package com.trodar.domain.model

data class Note(
    val id: Long = 0,
    val folderId: Long? = null,
    val title: String = "",
    val bgColor: Int = 0xFFFFFFFF.toInt(),
    val textColor: Int = 0xFF000000.toInt(),
    val frameId: Int = 0,
    val createdAt: Long,
    val updatedAt: Long
)
