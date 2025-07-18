package com.app.apiclient.data.network

import com.app.apiclient.data.model.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
class NetworkClient(
    private val apiService: ApiService
) {
    
    suspend fun executeRequest(
        request: ApiRequest,
        authConfig: AuthConfig = AuthConfig.None
    ): NetworkResult<Response<ResponseBody>> {
        return try {
            val headers = buildHeaders(request.headers, authConfig)
            val queryParams = request.queryParams
            val requestBody = createRequestBody(request.body, request.bodyType)
            
            val response = when (request.method) {
                HttpMethod.GET -> apiService.get(request.url, headers, queryParams)
                HttpMethod.POST -> apiService.post(request.url, requestBody, headers, queryParams)
                HttpMethod.PUT -> apiService.put(request.url, requestBody, headers, queryParams)
                HttpMethod.DELETE -> apiService.delete(request.url, headers, queryParams)
                HttpMethod.PATCH -> apiService.patch(request.url, requestBody, headers, queryParams)
                HttpMethod.HEAD -> apiService.head(request.url, headers, queryParams)
                HttpMethod.OPTIONS -> apiService.options(request.url, headers, queryParams)
            }
            
            NetworkResult.Success(response)
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
    
    private fun buildHeaders(
        requestHeaders: Map<String, String>,
        authConfig: AuthConfig
    ): Map<String, String> {
        val headers = requestHeaders.toMutableMap()
        
        when (authConfig) {
            is AuthConfig.None -> { /* No additional headers */ }
            is AuthConfig.BasicAuth -> {
                val credentials = "${authConfig.username}:${authConfig.password}"
                val encodedCredentials = android.util.Base64.encodeToString(
                    credentials.toByteArray(),
                    android.util.Base64.NO_WRAP
                )
                headers["Authorization"] = "Basic $encodedCredentials"
            }
            is AuthConfig.BearerToken -> {
                headers["Authorization"] = "Bearer ${authConfig.token}"
            }
            is AuthConfig.ApiKey -> {
                if (authConfig.location == ApiKeyLocation.HEADER) {
                    headers[authConfig.key] = authConfig.value
                }
                // Query param API keys are handled in the repository layer
            }
        }
        
        return headers
    }
    
    private fun createRequestBody(body: String?, bodyType: BodyType): RequestBody? {
        if (body.isNullOrBlank()) return null
        
        val mediaType = when (bodyType) {
            BodyType.NONE -> return null
            BodyType.RAW_JSON -> "application/json".toMediaType()
            BodyType.FORM_DATA -> "multipart/form-data".toMediaType()
            BodyType.URL_ENCODED -> "application/x-www-form-urlencoded".toMediaType()
        }
        
        return body.toRequestBody(mediaType)
    }
}

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val exception: Throwable) : NetworkResult<Nothing>()
}
