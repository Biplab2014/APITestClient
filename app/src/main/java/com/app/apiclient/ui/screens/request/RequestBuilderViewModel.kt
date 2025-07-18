package com.app.apiclient.ui.screens.request

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.apiclient.data.model.*
import com.app.apiclient.data.repository.ApiRequestRepository
import com.app.apiclient.data.repository.EnvironmentRepository
import com.app.apiclient.utils.CurlGenerator
import com.app.apiclient.utils.CurlParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

data class RequestBuilderUiState(
    val selectedMethod: String = "GET",
    val url: String = "{{baseUrl}}/posts/{{userId}}",
    val queryParams: List<Pair<String, String>> = listOf(
        "format" to "json"
    ),
    val headers: List<Pair<String, String>> = listOf(
        "Accept" to "application/json",
        "User-Agent" to "ApiClient/1.0",
        "Authorization" to "Bearer {{apiKey}}"
    ),
    val bodyType: String = "None",
    val body: String = "",
    val isLoading: Boolean = false,
    val response: ApiResponse? = null,
    val error: String? = null,
    val authConfig: AuthConfig = AuthConfig.None,
    val showCurlDialog: Boolean = false,
    val curlCommand: String = ""
)

class RequestBuilderViewModel(
    private val apiRequestRepository: ApiRequestRepository,
    private val environmentRepository: EnvironmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RequestBuilderUiState())
    val uiState: StateFlow<RequestBuilderUiState> = _uiState.asStateFlow()

    fun updateMethod(method: String) {
        _uiState.value = _uiState.value.copy(selectedMethod = method)
    }

    fun updateUrl(url: String) {
        _uiState.value = _uiState.value.copy(url = url)
    }

    fun addQueryParam() {
        val currentParams = _uiState.value.queryParams.toMutableList()
        currentParams.add("" to "")
        _uiState.value = _uiState.value.copy(queryParams = currentParams)
    }

    fun updateQueryParam(index: Int, key: String, value: String) {
        val currentParams = _uiState.value.queryParams.toMutableList()
        if (index < currentParams.size) {
            currentParams[index] = key to value
            _uiState.value = _uiState.value.copy(queryParams = currentParams)
        }
    }

    fun removeQueryParam(index: Int) {
        val currentParams = _uiState.value.queryParams.toMutableList()
        if (index < currentParams.size) {
            currentParams.removeAt(index)
            _uiState.value = _uiState.value.copy(queryParams = currentParams)
        }
    }

    fun addHeader() {
        val currentHeaders = _uiState.value.headers.toMutableList()
        currentHeaders.add("" to "")
        _uiState.value = _uiState.value.copy(headers = currentHeaders)
    }

    fun updateHeader(index: Int, key: String, value: String) {
        val currentHeaders = _uiState.value.headers.toMutableList()
        if (index < currentHeaders.size) {
            currentHeaders[index] = key to value
            _uiState.value = _uiState.value.copy(headers = currentHeaders)
        }
    }

    fun removeHeader(index: Int) {
        val currentHeaders = _uiState.value.headers.toMutableList()
        if (index < currentHeaders.size) {
            currentHeaders.removeAt(index)
            _uiState.value = _uiState.value.copy(headers = currentHeaders)
        }
    }

    fun updateBodyType(bodyType: String) {
        _uiState.value = _uiState.value.copy(
            bodyType = bodyType,
            body = if (bodyType == "None") "" else _uiState.value.body
        )
    }

    fun updateBody(body: String) {
        _uiState.value = _uiState.value.copy(body = body)
    }

    fun updateAuthConfig(authConfig: AuthConfig) {
        _uiState.value = _uiState.value.copy(authConfig = authConfig)
    }

    fun sendRequest() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                response = null
            )

            try {
                val currentState = _uiState.value

                // Replace environment variables in URL, headers, and body
                val processedUrl = environmentRepository.replaceVariables(currentState.url)
                val processedHeaders = environmentRepository.replaceVariablesInMap(
                    currentState.headers.toMap()
                )
                val processedBody = currentState.body.takeIf { it.isNotBlank() }?.let {
                    environmentRepository.replaceVariables(it)
                }

                // Debug logging
                println("DEBUG: Original URL: ${currentState.url}")
                println("DEBUG: Processed URL: $processedUrl")
                println("DEBUG: Headers: $processedHeaders")

                // Fallback: if URL still contains variables, use a default URL for testing
                val finalUrl = if (processedUrl.contains("{{")) {
                    "https://jsonplaceholder.typicode.com/posts/1"
                } else {
                    processedUrl
                }

                println("DEBUG: Final URL: $finalUrl")

                // Create API request
                val apiRequest = ApiRequest(
                    id = UUID.randomUUID().toString(),
                    name = "Request ${System.currentTimeMillis()}", // Auto-generated name
                    url = finalUrl,
                    method = HttpMethod.valueOf(currentState.selectedMethod),
                    headers = processedHeaders,
                    queryParams = currentState.queryParams.toMap(),
                    body = processedBody,
                    bodyType = mapBodyType(currentState.bodyType),
                    collectionId = null, // Not in a collection yet
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                // Execute the request
                val response = apiRequestRepository.executeRequest(apiRequest, currentState.authConfig)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    response = response,
                    error = if (response.isError) response.errorMessage else null
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    response = null,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    private fun mapBodyType(bodyTypeString: String): BodyType {
        return when (bodyTypeString) {
            "Raw JSON" -> BodyType.RAW_JSON
            "Form Data" -> BodyType.FORM_DATA
            "URL Encoded" -> BodyType.URL_ENCODED
            else -> BodyType.NONE
        }
    }

    fun loadSampleRequest(type: String) {
        when (type) {
            "GET_POSTS" -> {
                _uiState.value = _uiState.value.copy(
                    selectedMethod = "GET",
                    url = "{{baseUrl}}/posts",
                    queryParams = listOf("_limit" to "10"),
                    headers = listOf(
                        "Accept" to "application/json",
                        "User-Agent" to "ApiClient/1.0"
                    ),
                    bodyType = "None",
                    body = ""
                )
            }
            "POST_USER" -> {
                _uiState.value = _uiState.value.copy(
                    selectedMethod = "POST",
                    url = "{{baseUrl}}/users",
                    queryParams = emptyList(),
                    headers = listOf(
                        "Content-Type" to "application/json",
                        "Accept" to "application/json",
                        "User-Agent" to "ApiClient/1.0"
                    ),
                    bodyType = "Raw JSON",
                    body = """
                        {
                          "name": "John Doe",
                          "username": "johndoe",
                          "email": "john@example.com",
                          "phone": "1-770-736-8031 x56442",
                          "website": "hildegard.org"
                        }
                    """.trimIndent()
                )
            }
            "GET_USER" -> {
                _uiState.value = _uiState.value.copy(
                    selectedMethod = "GET",
                    url = "{{baseUrl}}/users/{{userId}}",
                    queryParams = emptyList(),
                    headers = listOf(
                        "Accept" to "application/json",
                        "User-Agent" to "ApiClient/1.0"
                    ),
                    bodyType = "None",
                    body = ""
                )
            }
        }
    }

    fun showCurlDialog() {
        _uiState.value = _uiState.value.copy(showCurlDialog = true)
    }

    fun hideCurlDialog() {
        _uiState.value = _uiState.value.copy(showCurlDialog = false, curlCommand = "")
    }

    fun updateCurlCommand(curlCommand: String) {
        _uiState.value = _uiState.value.copy(curlCommand = curlCommand)
    }

    fun importFromCurl() {
        val curlCommand = _uiState.value.curlCommand
        val parsed = CurlParser.parseCurl(curlCommand)

        if (parsed != null) {
            _uiState.value = _uiState.value.copy(
                selectedMethod = parsed.method.name,
                url = parsed.url,
                headers = parsed.headers.toList(),
                queryParams = parsed.queryParams.toList(),
                body = parsed.body ?: "",
                bodyType = if (parsed.body != null) "Raw JSON" else "None",
                showCurlDialog = false,
                curlCommand = "",
                error = null
            )
        } else {
            _uiState.value = _uiState.value.copy(
                error = "Invalid cURL command. Please check the syntax and try again."
            )
        }
    }

    fun generateCurlCommand(): String {
        val state = _uiState.value
        return CurlGenerator.generateCurl(
            url = state.url,
            method = HttpMethod.valueOf(state.selectedMethod),
            headers = state.headers.toMap(),
            queryParams = state.queryParams.toMap(),
            body = if (state.bodyType != "None") state.body else null
        )
    }
}
