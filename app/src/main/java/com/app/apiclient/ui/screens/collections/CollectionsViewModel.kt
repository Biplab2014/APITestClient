package com.app.apiclient.ui.screens.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.apiclient.data.model.RequestCollection
import com.app.apiclient.data.model.ApiRequest
import com.app.apiclient.data.repository.CollectionRepository
import com.app.apiclient.data.repository.ApiRequestRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CollectionWithRequestCount(
    val collection: RequestCollection,
    val requestCount: Int
)

data class CollectionsUiState(
    val collections: List<CollectionWithRequestCount> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val showCreateDialog: Boolean = false
)

class CollectionsViewModel(
    private val collectionRepository: CollectionRepository,
    private val apiRequestRepository: ApiRequestRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CollectionsUiState())
    val uiState: StateFlow<CollectionsUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    
    init {
        loadCollections()
    }
    
    private fun loadCollections() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                combine(
                    if (_searchQuery.value.isBlank()) {
                        collectionRepository.getAllCollections()
                    } else {
                        collectionRepository.searchCollections(_searchQuery.value)
                    },
                    apiRequestRepository.getAllRequests()
                ) { collections, requests ->
                    collections.map { collection ->
                        val requestCount = requests.count { it.collectionId == collection.id }
                        CollectionWithRequestCount(collection, requestCount)
                    }
                }.collect { collectionsWithCount ->
                    _uiState.value = _uiState.value.copy(
                        collections = collectionsWithCount,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load collections"
                )
            }
        }
    }
    
    fun searchCollections(query: String) {
        _searchQuery.value = query
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadCollections()
    }
    
    fun showCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = true)
    }
    
    fun hideCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = false)
    }
    
    fun createCollection(name: String, description: String) {
        viewModelScope.launch {
            try {
                collectionRepository.createCollection(name, description)
                hideCreateDialog()
                loadCollections()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to create collection"
                )
            }
        }
    }
    
    fun deleteCollection(collection: RequestCollection) {
        viewModelScope.launch {
            try {
                collectionRepository.deleteCollection(collection)
                loadCollections()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to delete collection"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
