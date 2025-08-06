package com.flashlight.flashalert.oncall.sms.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flashlight.flashalert.oncall.sms.core.result.Result
import com.flashlight.flashalert.oncall.sms.ui.theme.ThemedError
import com.flashlight.flashalert.oncall.sms.ui.theme.ThemedLoading
import com.flashlight.flashalert.oncall.sms.ui.theme.ThemedText

@Composable
fun LoadingComponent(
    modifier: Modifier = Modifier
) {
    ThemedLoading(modifier = modifier)
}

@Composable
fun ErrorComponent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    ThemedError(
        message = message,
        onRetry = onRetry,
        modifier = modifier
    )
}

@Composable
fun <T> ResultHandler(
    result: Result<T>,
    onRetry: () -> Unit,
    content: @Composable (T) -> Unit,
    modifier: Modifier = Modifier
) {
    when (result) {
        is Result.Loading -> {
            LoadingComponent(modifier = modifier)
        }
        is Result.Error -> {
            ErrorComponent(
                message = result.exception.message ?: "An error occurred",
                onRetry = onRetry,
                modifier = modifier
            )
        }
        is Result.Success -> {
            content(result.data as T)
        }
    }
}

/**
 * Empty State Component
 */
@Composable
fun EmptyStateComponent(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ThemedText(
            text = title,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        ThemedText(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Success State Component
 */
@Composable
fun SuccessComponent(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ThemedText(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
    }
} 