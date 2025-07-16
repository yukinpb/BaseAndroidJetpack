package com.basecompose.baseproject.features.home.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.basecompose.baseproject.core.ui.components.ResultHandler
import com.basecompose.baseproject.features.home.presentation.viewmodel.HomeViewModel
import com.basecompose.baseproject.ui.theme.ThemedButton
import com.basecompose.baseproject.ui.theme.ThemedText
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.DetailScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination<RootGraph>(start = true)
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { ThemedText("Base Project") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ResultHandler(
                result = uiState,
                onRetry = { viewModel.loadData() },
                content = { data ->
                    ThemedText(
                        text = "Welcome to Base Project!",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ThemedText(
                        text = "Data: $data",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Navigation buttons
                    ThemedButton(
                        onClick = {
                            navigator.navigate(
                                DetailScreenDestination(
                                    id = "12345"
                                )
                            )
                        }
                    ) {
                        ThemedText("Go to Detail")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    ThemedButton(
                        onClick = {
                            navigator.navigate(
                                SettingsScreenDestination()
                            )
                        }
                    ) {
                        ThemedText("Go to Settings")
                    }
                }
            )
        }
    }
} 