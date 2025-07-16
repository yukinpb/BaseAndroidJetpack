package com.basecompose.baseproject.features.home.presentation.viewmodel

import com.basecompose.baseproject.core.network.ApiService
import com.basecompose.baseproject.core.result.Result
import com.basecompose.baseproject.core.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiService: ApiService
) : BaseViewModel() {

    private val _uiState = createStateFlow<String>(Result.Loading)
    val uiState: StateFlow<Result<String>> = _uiState.asReadOnlyStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        launchWithState(_uiState) {
            // Simulate API call
            kotlinx.coroutines.delay(1000)
            "Sample data from API"
        }
    }
} 