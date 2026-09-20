package com.didiforget.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.didiforget.R
import com.didiforget.data.model.Activity
import com.didiforget.ui.components.ActivityCard
import com.didiforget.ui.components.PrimaryButton
import com.didiforget.ui.theme.Dimens
import com.didiforget.ui.theme.DidIForgetOnSurface
import com.didiforget.ui.theme.DidIForgetOnSurfaceMuted
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

    Scaffold(
        modifier = modifier.fillMaxSize(),
        timeText = { TimeText() }
    ) {
        ScalingLazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = Dimens.ScreenHorizontalPadding)) {
            item {
                Text(
                    text = stringResource(R.string.home_title),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.title1,
                    color = DidIForgetOnSurface,
                    textAlign = TextAlign.Center
                )
            }

            item {
                PrimaryButton(
                    text = stringResource(R.string.home_new_activity),
                    icon = R.drawable.ic_plus,
                    onClick = onNewActivityClick
                )
            }

            when (val current = state) {
                is UiState.Loading -> Unit
                is UiState.Error -> item {
                    Text(
                        text = current.error.message,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center
                    )
                }
                is UiState.Success -> {
                    if (current.data.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.home_empty_state),
                                modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.body2,
                                color = DidIForgetOnSurfaceMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        items(current.data) { activity ->
                            ActivityCard(activity = activity, onClick = { onActivityClick(activity) })
                        }
                    }
                }
            }
        }
    }
}
