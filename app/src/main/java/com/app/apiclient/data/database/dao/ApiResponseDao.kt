package com.app.apiclient.data.database.dao

import androidx.room.*
import com.app.apiclient.data.model.ApiResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface ApiResponseDao {
    
    @Query("SELECT * FROM api_responses ORDER BY timestamp DESC")
    fun getAllResponses(): Flow<List<ApiResponse>>
    
    @Query("SELECT * FROM api_responses WHERE requestId = :requestId ORDER BY timestamp DESC")
    fun getResponsesByRequest(requestId: String): Flow<List<ApiResponse>>
    
    @Query("SELECT * FROM api_responses WHERE id = :id")
    suspend fun getResponseById(id: String): ApiResponse?
    
    @Query("SELECT * FROM api_responses ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentResponses(limit: Int = 50): Flow<List<ApiResponse>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResponse(response: ApiResponse)
    
    @Delete
    suspend fun deleteResponse(response: ApiResponse)
    
    @Query("DELETE FROM api_responses WHERE id = :id")
    suspend fun deleteResponseById(id: String)
    
    @Query("DELETE FROM api_responses WHERE requestId = :requestId")
    suspend fun deleteResponsesByRequest(requestId: String)
    
    @Query("DELETE FROM api_responses WHERE timestamp < :timestamp")
    suspend fun deleteOldResponses(timestamp: Long)
}
