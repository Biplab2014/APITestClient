package com.app.apiclient.data.repository

import com.app.apiclient.data.database.dao.CollectionDao
import com.app.apiclient.data.database.dao.ApiRequestDao
import com.app.apiclient.data.model.RequestCollection
import kotlinx.coroutines.flow.Flow
import java.util.*
class CollectionRepository(
    private val collectionDao: CollectionDao,
    private val apiRequestDao: ApiRequestDao
) {
    
    fun getAllCollections(): Flow<List<RequestCollection>> = collectionDao.getAllCollections()

    suspend fun getCollectionById(id: String): RequestCollection? = collectionDao.getCollectionById(id)

    fun searchCollections(query: String): Flow<List<RequestCollection>> =
        collectionDao.searchCollections(query)

    suspend fun createCollection(name: String, description: String? = null): RequestCollection {
        val collection = RequestCollection(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        collectionDao.insertCollection(collection)
        return collection
    }
    
    suspend fun updateCollection(collection: RequestCollection) {
        val updatedCollection = collection.copy(
            updatedAt = System.currentTimeMillis()
        )
        collectionDao.updateCollection(updatedCollection)
    }

    suspend fun deleteCollection(collection: RequestCollection) {
        // Delete all requests in this collection first
        apiRequestDao.deleteRequestsByCollection(collection.id)
        // Then delete the collection
        collectionDao.deleteCollection(collection)
    }
    
    suspend fun deleteCollectionById(id: String) {
        apiRequestDao.deleteRequestsByCollection(id)
        collectionDao.deleteCollectionById(id)
    }
}
