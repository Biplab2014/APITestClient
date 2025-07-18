package com.app.apiclient.ui.screens.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.apiclient.data.repository.CollectionRepository
import com.app.apiclient.data.repository.ApiRequestRepository

class CollectionsViewModelFactory(
    private val collectionRepository: CollectionRepository,
    private val apiRequestRepository: ApiRequestRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CollectionsViewModel::class.java)) {
            return CollectionsViewModel(collectionRepository, apiRequestRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
