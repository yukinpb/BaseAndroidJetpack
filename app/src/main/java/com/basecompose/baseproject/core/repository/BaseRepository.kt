package com.basecompose.baseproject.core.repository

import com.basecompose.baseproject.core.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseRepository {
    
    protected suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                Result.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                Result.Error(Exception(throwable.message))
            }
        }
    }
} 