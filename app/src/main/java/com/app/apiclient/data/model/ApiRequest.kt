package com.app.apiclient.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "api_requests")
@Serializable
data class ApiRequest(
    @PrimaryKey
    val id: String,
    val name: String,
    val url: String,
    val method: HttpMethod,
    val headers: Map<String, String> = emptyMap(),
    val queryParams: Map<String, String> = emptyMap(),
    val body: String? = null,
    val bodyType: BodyType = BodyType.NONE,
    val collectionId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class BodyType {
    NONE,
    RAW_JSON,
    FORM_DATA,
    URL_ENCODED
}
