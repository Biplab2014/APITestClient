package com.app.apiclient.data.repository

import com.app.apiclient.data.database.dao.ApiRequestDao
import com.app.apiclient.data.database.dao.ApiResponseDao
import com.app.apiclient.data.model.*
import com.app.apiclient.data.network.NetworkClient
import com.app.apiclient.data.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import java.util.*
class ApiRequestRepository(
    private val apiRequestDao: ApiRequestDao,
    private val apiResponseDao: ApiResponseDao,
    private val networkClient: NetworkClient
) {
    
    fun getAllRequests(): Flow<List<ApiRequest>> = apiRequestDao.getAllRequests()
    
    fun getRequestsByCollection(collectionId: String): Flow<List<ApiRequest>> =
        apiRequestDao.getRequestsByCollection(collectionId)
    
    suspend fun getRequestById(id: String): ApiRequest? = apiRequestDao.getRequestById(id)
    
    fun searchRequests(query: String): Flow<List<ApiRequest>> = apiRequestDao.searchRequests(query)
    
    fun getRecentRequests(limit: Int = 10): Flow<List<ApiRequest>> =
        apiRequestDao.getRecentRequests(limit)
    
    suspend fun saveRequest(request: ApiRequest) {
        val updatedRequest = request.copy(
            updatedAt = System.currentTimeMillis()
        )
        apiRequestDao.insertRequest(updatedRequest)
    }
    
    suspend fun updateRequest(request: ApiRequest) {
        val updatedRequest = request.copy(
            updatedAt = System.currentTimeMillis()
        )
        apiRequestDao.updateRequest(updatedRequest)
    }
    
    suspend fun deleteRequest(request: ApiRequest) {
        apiRequestDao.deleteRequest(request)
        // Also delete associated responses
        apiResponseDao.deleteResponsesByRequest(request.id)
    }
    
    suspend fun executeRequest(
        request: ApiRequest,
        authConfig: AuthConfig = AuthConfig.None
    ): ApiResponse {
        val startTime = System.currentTimeMillis()
        
        return when (val result = networkClient.executeRequest(request, authConfig)) {
            is NetworkResult.Success -> {
                val response = result.data
                val endTime = System.currentTimeMillis()
                val responseTime = endTime - startTime
                
                val responseBody = try {
                    response.body()?.string()
                } catch (e: Exception) {
                    null
                }
                
                val responseSize = responseBody?.toByteArray()?.size?.toLong() ?: 0L
                
                val responseHeaders = mutableMapOf<String, String>()
                response.headers().forEach { (name, value) ->
                    responseHeaders[name] = value
                }

                val apiResponse = ApiResponse(
                    id = UUID.randomUUID().toString(),
                    requestId = request.id,
                    statusCode = response.code(),
                    statusMessage = response.message(),
                    headers = responseHeaders,
                    body = responseBody,
                    responseTime = responseTime,
                    responseSize = responseSize,
                    timestamp = endTime,
                    isError = false
                )
                
                // Save response to database
                apiResponseDao.insertResponse(apiResponse)
                
                apiResponse
            }
            is NetworkResult.Error -> {
                val endTime = System.currentTimeMillis()
                val responseTime = endTime - startTime
                
                val apiResponse = ApiResponse(
                    id = UUID.randomUUID().toString(),
                    requestId = request.id,
                    statusCode = 0,
                    statusMessage = "Network Error",
                    responseTime = responseTime,
                    responseSize = 0L,
                    timestamp = endTime,
                    isError = true,
                    errorMessage = result.exception.message
                )
                
                // Save error response to database
                apiResponseDao.insertResponse(apiResponse)
                
                apiResponse
            }
        }
    }
    
    suspend fun duplicateRequest(request: ApiRequest): ApiRequest {
        val duplicatedRequest = request.copy(
            id = UUID.randomUUID().toString(),
            name = "${request.name} (Copy)",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        apiRequestDao.insertRequest(duplicatedRequest)
        return duplicatedRequest
    }
}
