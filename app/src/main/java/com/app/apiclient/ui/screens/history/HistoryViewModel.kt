package com.app.apiclient.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.apiclient.data.model.ApiRequest
import com.app.apiclient.data.model.ApiResponse
import com.app.apiclient.data.repository.ApiRequestRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class RequestWithResponse(
    val request: ApiRequest,
    val response: ApiResponse?
)

data class HistoryUiState(
    val requestsWithResponses: List<RequestWithResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedRequest: RequestWithResponse? = null
)

class HistoryViewModel(
    private val apiRequestRepository: ApiRequestRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    
    init {
        loadHistory()
    }
    
    private fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val requests = if (_searchQuery.value.isBlank()) {
                    apiRequestRepository.getRecentRequests(50)
                } else {
                    apiRequestRepository.searchRequests(_searchQuery.value)
                }
                
                requests.collect { requestList ->
                    // For now, we'll create mock responses since we don't have a response repository method
                    // In a real implementation, you'd fetch the actual responses from the database
                    val requestsWithResponses = requestList.map { request ->
                        RequestWithResponse(
                            request = request,
                            response = null // TODO: Fetch actual response from database
                        )
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        requestsWithResponses = requestsWithResponses,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load history"
                )
            }
        }
    }
    
    fun searchHistory(query: String) {
        _searchQuery.value = query
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadHistory()
    }
    
    fun selectRequest(requestWithResponse: RequestWithResponse) {
        _uiState.value = _uiState.value.copy(selectedRequest = requestWithResponse)
    }
    
    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedRequest = null)
    }
    
    fun deleteRequest(request: ApiRequest) {
        viewModelScope.launch {
            try {
                apiRequestRepository.deleteRequest(request)
                loadHistory()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to delete request"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
