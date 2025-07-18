package com.app.apiclient.data.model

import kotlinx.serialization.Serializable

@Serializable
sealed class AuthConfig {
    @Serializable
    object None : AuthConfig()
    
    @Serializable
    data class BasicAuth(
        val username: String,
        val password: String
    ) : AuthConfig()
    
    @Serializable
    data class BearerToken(
        val token: String
    ) : AuthConfig()
    
    @Serializable
    data class ApiKey(
        val key: String,
        val value: String,
        val location: ApiKeyLocation
    ) : AuthConfig()
}

enum class ApiKeyLocation {
    HEADER,
    QUERY_PARAM
}
