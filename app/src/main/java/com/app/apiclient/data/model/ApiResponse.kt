package com.app.apiclient.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "api_responses")
@Serializable
data class ApiResponse(
    @PrimaryKey
    val id: String,
    val requestId: String,
    val statusCode: Int,
    val statusMessage: String,
    val headers: Map<String, String> = emptyMap(),
    val body: String? = null,
    val responseTime: Long, // in milliseconds
    val responseSize: Long, // in bytes
    val timestamp: Long = System.currentTimeMillis(),
    val isError: Boolean = false,
    val errorMessage: String? = null
)
