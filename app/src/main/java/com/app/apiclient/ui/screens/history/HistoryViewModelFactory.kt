package com.app.apiclient.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.apiclient.data.repository.ApiRequestRepository

class HistoryViewModelFactory(
    private val apiRequestRepository: ApiRequestRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(apiRequestRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
