package com.app.apiclient.ui.screens.request

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.apiclient.data.repository.ApiRequestRepository
import com.app.apiclient.data.repository.EnvironmentRepository

class RequestBuilderViewModelFactory(
    private val apiRequestRepository: ApiRequestRepository,
    private val environmentRepository: EnvironmentRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RequestBuilderViewModel::class.java)) {
            return RequestBuilderViewModel(apiRequestRepository, environmentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
