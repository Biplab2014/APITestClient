package com.app.apiclient.di

import android.content.Context
import com.app.apiclient.data.database.ApiClientDatabase
import com.app.apiclient.data.network.ApiService
import com.app.apiclient.data.network.NetworkClient
import com.app.apiclient.data.repository.ApiRequestRepository
import com.app.apiclient.data.repository.CollectionRepository
import com.app.apiclient.data.repository.EnvironmentRepository

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Simple dependency injection container
 */
class AppContainer(private val context: Context) {
    
    // Network dependencies
    
    private val okHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.example.com/") // This will be overridden by @Url
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    private val apiService by lazy {
        retrofit.create(ApiService::class.java)
    }
    
    val networkClient by lazy {
        NetworkClient(apiService)
    }
    
    // Database dependencies
    private val database by lazy {
        ApiClientDatabase.getDatabase(context)
    }
    
    private val apiRequestDao by lazy { database.apiRequestDao() }
    private val apiResponseDao by lazy { database.apiResponseDao() }
    private val collectionDao by lazy { database.collectionDao() }
    private val environmentDao by lazy { database.environmentDao() }
    
    // Repository dependencies
    val apiRequestRepository by lazy {
        ApiRequestRepository(apiRequestDao, apiResponseDao, networkClient)
    }
    
    val collectionRepository by lazy {
        CollectionRepository(collectionDao, apiRequestDao)
    }
    
    val environmentRepository by lazy {
        EnvironmentRepository(environmentDao)
    }

    // Initialize default data
    suspend fun initializeDefaultData() {
        // Check if any environments exist
        val activeEnvironment = environmentRepository.getActiveEnvironment()

        if (activeEnvironment == null) {
            // Create a default environment if none exists
            val defaultEnv = environmentRepository.createEnvironment(
                name = "Default",
                variables = mapOf(
                    "baseUrl" to "https://jsonplaceholder.typicode.com",
                    "apiKey" to "your-api-key-here",
                    "userId" to "1"
                )
            )
            environmentRepository.setActiveEnvironment(defaultEnv.id)
        }
    }
}
