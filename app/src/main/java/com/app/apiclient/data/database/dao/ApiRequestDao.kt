package com.app.apiclient.data.database.dao

import androidx.room.*
import com.app.apiclient.data.model.ApiRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface ApiRequestDao {
    
    @Query("SELECT * FROM api_requests ORDER BY updatedAt DESC")
    fun getAllRequests(): Flow<List<ApiRequest>>
    
    @Query("SELECT * FROM api_requests WHERE collectionId = :collectionId ORDER BY updatedAt DESC")
    fun getRequestsByCollection(collectionId: String): Flow<List<ApiRequest>>
    
    @Query("SELECT * FROM api_requests WHERE id = :id")
    suspend fun getRequestById(id: String): ApiRequest?
    
    @Query("SELECT * FROM api_requests WHERE name LIKE '%' || :query || '%' OR url LIKE '%' || :query || '%'")
    fun searchRequests(query: String): Flow<List<ApiRequest>>
    
    @Query("SELECT * FROM api_requests ORDER BY updatedAt DESC LIMIT :limit")
    fun getRecentRequests(limit: Int = 10): Flow<List<ApiRequest>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: ApiRequest)
    
    @Update
    suspend fun updateRequest(request: ApiRequest)
    
    @Delete
    suspend fun deleteRequest(request: ApiRequest)
    
    @Query("DELETE FROM api_requests WHERE id = :id")
    suspend fun deleteRequestById(id: String)
    
    @Query("DELETE FROM api_requests WHERE collectionId = :collectionId")
    suspend fun deleteRequestsByCollection(collectionId: String)
}
