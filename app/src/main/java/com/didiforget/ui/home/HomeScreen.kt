package com.didiforget.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.Text
import com.didiforget.R
import com.didiforget.data.model.Activity
import com.didiforget.ui.components.ActivityCard
import com.didiforget.viewmodel.HomeViewModel
import com.didiforget.viewmodel.UiState

/**
 * Pantalla principal (ver README, sección "Flujo de usuario" paso 1):
 * lista de actividades guardadas + botón para crear una nueva.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onActivityClick: (Activity) -> Unit,
    onNewActivityClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    ScalingLazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            ListHeader { Text(stringResource(R.string.home_title)) }
        }

        item {
            Chip(
                onClick = onNewActivityClick,
                colors = ChipDefaults.primaryChipColors(),
                label = { Text(stringResource(R.string.home_new_activity)) }
            )
        }

        when (val current = state) {
            is UiState.Loading -> Unit
            is UiState.Error -> item { Text(current.error.message) }
            is UiState.Success -> {
                if (current.data.isEmpty()) {
                    item { Text(stringResource(R.string.home_empty_state)) }
                } else {
                    items(current.data) { activity ->
                        ActivityCard(activity = activity, onClick = { onActivityClick(activity) })
                    }
                }
            }
        }
    }
}
