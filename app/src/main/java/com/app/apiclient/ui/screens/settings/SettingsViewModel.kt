package com.app.apiclient.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.apiclient.data.repository.ApiRequestRepository
import com.app.apiclient.data.repository.CollectionRepository
import com.app.apiclient.data.repository.EnvironmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val autoSaveRequests: Boolean = true,
    val requestTimeout: Int = 30, // seconds
    val sslVerification: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showTimeoutDialog: Boolean = false,
    val showClearHistoryDialog: Boolean = false,
    val showExportDialog: Boolean = false,
    val showImportDialog: Boolean = false,
    val totalRequests: Int = 0,
    val totalCollections: Int = 0,
    val totalEnvironments: Int = 0
)

class SettingsViewModel(
    private val apiRequestRepository: ApiRequestRepository,
    private val collectionRepository: CollectionRepository,
    private val environmentRepository: EnvironmentRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
        loadStatistics()
    }
    
    private fun loadSettings() {
        // In a real app, these would be loaded from SharedPreferences or DataStore
        // For now, we'll use default values
        _uiState.value = _uiState.value.copy(
            isDarkMode = false,
            autoSaveRequests = true,
            requestTimeout = 30,
            sslVerification = true
        )
    }
    
    private fun loadStatistics() {
        viewModelScope.launch {
            try {
                // Load statistics from repositories
                apiRequestRepository.getAllRequests().collect { requests ->
                    _uiState.value = _uiState.value.copy(totalRequests = requests.size)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load statistics: ${e.message}"
                )
            }
        }
        
        viewModelScope.launch {
            try {
                collectionRepository.getAllCollections().collect { collections ->
                    _uiState.value = _uiState.value.copy(totalCollections = collections.size)
                }
            } catch (e: Exception) {
                // Handle error silently for statistics
            }
        }
        
        viewModelScope.launch {
            try {
                environmentRepository.getAllEnvironments().collect { environments ->
                    _uiState.value = _uiState.value.copy(totalEnvironments = environments.size)
                }
            } catch (e: Exception) {
                // Handle error silently for statistics
            }
        }
    }
    
    fun toggleDarkMode() {
        val newValue = !_uiState.value.isDarkMode
        _uiState.value = _uiState.value.copy(isDarkMode = newValue)
        // TODO: Save to preferences and apply theme
    }
    
    fun toggleAutoSave() {
        val newValue = !_uiState.value.autoSaveRequests
        _uiState.value = _uiState.value.copy(autoSaveRequests = newValue)
        // TODO: Save to preferences
    }
    
    fun toggleSslVerification() {
        val newValue = !_uiState.value.sslVerification
        _uiState.value = _uiState.value.copy(sslVerification = newValue)
        // TODO: Save to preferences and update network client
    }
    
    fun showTimeoutDialog() {
        _uiState.value = _uiState.value.copy(showTimeoutDialog = true)
    }
    
    fun hideTimeoutDialog() {
        _uiState.value = _uiState.value.copy(showTimeoutDialog = false)
    }
    
    fun updateTimeout(timeout: Int) {
        _uiState.value = _uiState.value.copy(
            requestTimeout = timeout,
            showTimeoutDialog = false
        )
        // TODO: Save to preferences and update network client
    }
    
    fun showClearHistoryDialog() {
        _uiState.value = _uiState.value.copy(showClearHistoryDialog = true)
    }
    
    fun hideClearHistoryDialog() {
        _uiState.value = _uiState.value.copy(showClearHistoryDialog = false)
    }
    
    fun clearHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Clear all requests and responses
                // Note: This is a simplified implementation
                // In a real app, you'd have a method to clear all history
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    showClearHistoryDialog = false,
                    totalRequests = 0
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to clear history: ${e.message}"
                )
            }
        }
    }
    
    fun showExportDialog() {
        _uiState.value = _uiState.value.copy(showExportDialog = true)
    }
    
    fun hideExportDialog() {
        _uiState.value = _uiState.value.copy(showExportDialog = false)
    }
    
    fun exportData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // TODO: Implement actual export functionality
                // This would export collections and requests to JSON format
                kotlinx.coroutines.delay(2000) // Simulate export process
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    showExportDialog = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to export data: ${e.message}"
                )
            }
        }
    }
    
    fun showImportDialog() {
        _uiState.value = _uiState.value.copy(showImportDialog = true)
    }
    
    fun hideImportDialog() {
        _uiState.value = _uiState.value.copy(showImportDialog = false)
    }
    
    fun importData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // TODO: Implement actual import functionality
                // This would import from Postman collections or other formats
                kotlinx.coroutines.delay(2000) // Simulate import process
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    showImportDialog = false
                )
                
                // Reload statistics after import
                loadStatistics()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to import data: ${e.message}"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
