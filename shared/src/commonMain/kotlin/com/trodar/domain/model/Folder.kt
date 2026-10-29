package com.trodar.domain.model

data class Folder(
    val id: Long = 0,
    val parentId: Long? = null,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long
)
