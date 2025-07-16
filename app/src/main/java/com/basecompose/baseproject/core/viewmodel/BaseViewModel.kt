package com.basecompose.baseproject.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basecompose.baseproject.core.result.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    
    protected fun <T> launchWithState(
        stateFlow: MutableStateFlow<Result<T>>,
        apiCall: suspend () -> T
    ) {
        viewModelScope.launch {
            stateFlow.value = Result.Loading
            try {
                val result = apiCall()
                stateFlow.value = Result.Success(result)
            } catch (e: Exception) {
                stateFlow.value = Result.Error(e)
            }
        }
    }
    
    protected fun <T> createStateFlow(initialValue: Result<T>): MutableStateFlow<Result<T>> {
        return MutableStateFlow(initialValue)
    }
    
    protected fun <T> MutableStateFlow<Result<T>>.asReadOnlyStateFlow(): StateFlow<Result<T>> {
        return this.asStateFlow()
    }
} 