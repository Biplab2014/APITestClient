package com.app.apiclient.data.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    @GET
    suspend fun get(
        @Url url: String,
        @HeaderMap headers: Map<String, String> = emptyMap(),
        @QueryMap queryParams: Map<String, String> = emptyMap()
    ): Response<ResponseBody>
    
    @POST
    suspend fun post(
        @Url url: String,
        @Body body: RequestBody? = null,
        @HeaderMap headers: Map<String, String> = emptyMap(),
        @QueryMap queryParams: Map<String, String> = emptyMap()
    ): Response<ResponseBody>
    
    @PUT
    suspend fun put(
        @Url url: String,
        @Body body: RequestBody? = null,
        @HeaderMap headers: Map<String, String> = emptyMap(),
        @QueryMap queryParams: Map<String, String> = emptyMap()
    ): Response<ResponseBody>
    
    @DELETE
    suspend fun delete(
        @Url url: String,
        @HeaderMap headers: Map<String, String> = emptyMap(),
        @QueryMap queryParams: Map<String, String> = emptyMap()
    ): Response<ResponseBody>
    
    @PATCH
    suspend fun patch(
        @Url url: String,
        @Body body: RequestBody? = null,
        @HeaderMap headers: Map<String, String> = emptyMap(),
        @QueryMap queryParams: Map<String, String> = emptyMap()
    ): Response<ResponseBody>
    
    @HEAD
    suspend fun head(
        @Url url: String,
        @HeaderMap headers: Map<String, String> = emptyMap(),
        @QueryMap queryParams: Map<String, String> = emptyMap()
    ): Response<ResponseBody>
    
    @OPTIONS
    suspend fun options(
        @Url url: String,
        @HeaderMap headers: Map<String, String> = emptyMap(),
        @QueryMap queryParams: Map<String, String> = emptyMap()
    ): Response<ResponseBody>
}
