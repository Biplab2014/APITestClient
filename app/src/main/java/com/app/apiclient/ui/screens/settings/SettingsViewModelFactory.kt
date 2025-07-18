package com.app.apiclient.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.apiclient.data.repository.ApiRequestRepository
import com.app.apiclient.data.repository.CollectionRepository
import com.app.apiclient.data.repository.EnvironmentRepository

class SettingsViewModelFactory(
    private val apiRequestRepository: ApiRequestRepository,
    private val collectionRepository: CollectionRepository,
    private val environmentRepository: EnvironmentRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(
                apiRequestRepository,
                collectionRepository,
                environmentRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
