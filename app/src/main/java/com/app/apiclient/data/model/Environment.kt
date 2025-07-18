package com.app.apiclient.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "environments")
@Serializable
data class Environment(
    @PrimaryKey
    val id: String,
    val name: String,
    val variables: Map<String, String> = emptyMap(),
    val isActive: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
