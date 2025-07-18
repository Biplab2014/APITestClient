package com.app.apiclient.ui.screens.auth

import androidx.lifecycle.ViewModel
import com.app.apiclient.data.model.AuthConfig
import com.app.apiclient.data.model.ApiKeyLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AuthenticationUiState(
    val authType: String = "None",
    val basicAuthUsername: String = "",
    val basicAuthPassword: String = "",
    val bearerToken: String = "",
    val apiKeyKey: String = "",
    val apiKeyValue: String = "",
    val apiKeyLocation: ApiKeyLocation = ApiKeyLocation.HEADER
)

class AuthenticationViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthenticationUiState())
    val uiState: StateFlow<AuthenticationUiState> = _uiState.asStateFlow()
    
    fun updateAuthType(authType: String) {
        _uiState.value = _uiState.value.copy(authType = authType)
    }
    
    fun updateBasicAuthUsername(username: String) {
        _uiState.value = _uiState.value.copy(basicAuthUsername = username)
    }
    
    fun updateBasicAuthPassword(password: String) {
        _uiState.value = _uiState.value.copy(basicAuthPassword = password)
    }
    
    fun updateBearerToken(token: String) {
        _uiState.value = _uiState.value.copy(bearerToken = token)
    }
    
    fun updateApiKeyKey(key: String) {
        _uiState.value = _uiState.value.copy(apiKeyKey = key)
    }
    
    fun updateApiKeyValue(value: String) {
        _uiState.value = _uiState.value.copy(apiKeyValue = value)
    }
    
    fun updateApiKeyLocation(location: ApiKeyLocation) {
        _uiState.value = _uiState.value.copy(apiKeyLocation = location)
    }
    
    fun getAuthConfig(): AuthConfig {
        val state = _uiState.value
        return when (state.authType) {
            "Basic Auth" -> AuthConfig.BasicAuth(
                username = state.basicAuthUsername,
                password = state.basicAuthPassword
            )
            "Bearer Token" -> AuthConfig.BearerToken(
                token = state.bearerToken
            )
            "API Key" -> AuthConfig.ApiKey(
                key = state.apiKeyKey,
                value = state.apiKeyValue,
                location = state.apiKeyLocation
            )
            else -> AuthConfig.None
        }
    }
    
    fun loadAuthConfig(authConfig: AuthConfig) {
        when (authConfig) {
            is AuthConfig.None -> {
                _uiState.value = _uiState.value.copy(authType = "None")
            }
            is AuthConfig.BasicAuth -> {
                _uiState.value = _uiState.value.copy(
                    authType = "Basic Auth",
                    basicAuthUsername = authConfig.username,
                    basicAuthPassword = authConfig.password
                )
            }
            is AuthConfig.BearerToken -> {
                _uiState.value = _uiState.value.copy(
                    authType = "Bearer Token",
                    bearerToken = authConfig.token
                )
            }
            is AuthConfig.ApiKey -> {
                _uiState.value = _uiState.value.copy(
                    authType = "API Key",
                    apiKeyKey = authConfig.key,
                    apiKeyValue = authConfig.value,
                    apiKeyLocation = authConfig.location
                )
            }
        }
    }
}
