package com.basecompose.baseproject.core.network

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Body
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Base API methods - customize as needed
    @GET("{endpoint}")
    suspend fun get(@Path("endpoint") endpoint: String): String
    
    @POST("{endpoint}")
    suspend fun post(@Path("endpoint") endpoint: String, @Body body: Any): String
    
    @PUT("{endpoint}")
    suspend fun put(@Path("endpoint") endpoint: String, @Body body: Any): String
    
    @DELETE("{endpoint}")
    suspend fun delete(@Path("endpoint") endpoint: String): String
} 