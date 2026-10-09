package com.didiforget.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.didiforget.R
import com.didiforget.data.model.Activity
import com.didiforget.ui.components.ActivityTile
import com.didiforget.ui.components.PrimaryIconButton
import com.didiforget.ui.theme.DidIForgetOnSurfaceMuted
import com.didiforget.ui.theme.responsiveGridPadding
import com.didiforget.ui.theme.responsiveHorizontalPadding
import com.didiforget.viewmodel.HomeViewModel
import com.didiforget.viewmodel.UiState

private const val GRID_COLUMNS = 2

/**
 * Pantalla principal (ver README, sección "Flujo de usuario" paso 1):
 * cuadrícula de 2 columnas con las actividades guardadas (un toque abre la
 * actividad) y, debajo, el botón para crear una nueva.
 *
 * Nota de diseño (exploración "Liquid glass actual"): no hay un título visible
 * — la hora del sistema y la cuadrícula de íconos ya dan todo el contexto que
 * esta pantalla necesita. El título sigue existiendo como encabezado
 * accesible invisible, para quien navegue con lector de pantalla.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onActivityClick: (Activity) -> Unit,
    onNewActivityClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val homeTitle = stringResource(R.string.home_title)
    val horizontalPadding = Modifier.padding(horizontal = responsiveHorizontalPadding())

    Scaffold(
        modifier = modifier.fillMaxSize(),
        timeText = { TimeText() }
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(responsiveGridPadding())
        ) {
            item {
                // Encabezado accesible invisible: TalkBack lo anuncia como título de
                // la pantalla, pero no ocupa espacio ni se dibuja (ver nota de diseño).
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.dp)
                        .semantics(mergeDescendants = true) {
                            heading()
                            contentDescription = homeTitle
                        }
                )
            }

            when (val current = state) {
                is UiState.Loading -> Unit
                is UiState.Error -> item {
                    Text(
                        text = current.error.message,
                        modifier = Modifier.fillMaxWidth().then(horizontalPadding),
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center
                    )
                }
                is UiState.Success -> {
                    if (current.data.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.home_empty_state),
                                modifier = Modifier.fillMaxWidth().then(horizontalPadding),
                                style = MaterialTheme.typography.body2,
                                color = DidIForgetOnSurfaceMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        items(current.data.chunked(GRID_COLUMNS)) { rowActivities ->
                            ActivityGridRow(rowActivities, onActivityClick)
                        }
                    }
                }
            }

            item {
                PrimaryIconButton(
                    icon = R.drawable.ic_plus,
                    contentDescription = stringResource(R.string.home_new_activity),
                    onClick = onNewActivityClick
                )
            }
        }
    }
}

/** Una fila de la cuadrícula; si queda incompleta se rellena para que las casillas mantengan el mismo ancho. */
@Composable
private fun ActivityGridRow(
    activities: List<Activity>,
    onActivityClick: (Activity) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = responsiveGridPadding()),
        horizontalArrangement = Arrangement.spacedBy(responsiveGridPadding())
    ) {
        activities.forEach { activity ->
            ActivityTile(
                activity = activity,
                onClick = { onActivityClick(activity) },
                modifier = Modifier.weight(1f)
            )
        }
        repeat(GRID_COLUMNS - activities.size) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
