package com.didiforget.ui.checklist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.Text
import com.didiforget.ui.components.ChecklistItemRow
import com.didiforget.ui.components.PrimaryButton
import com.didiforget.ui.result.ResultScreen
import com.didiforget.viewmodel.ChecklistViewModel
import com.didiforget.viewmodel.UiState

/**
 * Pantalla de verificación (README, "Flujo de usuario" paso 4): el usuario
 * marca cada objeto conforme comprueba que lo lleva.
 *
 * Nota de diseño: en vez de navegar a una ruta separada para
 * [com.didiforget.ui.result.ResultScreen], esta pantalla la muestra "en
 * línea" cuando `viewModel.lastCheck` deja de ser null. Así ambas pantallas
 * comparten la misma instancia de [ChecklistViewModel] sin tener que
 * compartir un ViewModel entre dos destinos de navegación (más simple que
 * usar un scope de NavBackStackEntry compartido). El usuario vuelve a la
 * checklist con el gesto de swipe-to-dismiss propio de Wear OS.
 */
@Composable
fun ChecklistScreen(
    activityId: Long,
    viewModel: ChecklistViewModel,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(activityId) { viewModel.load(activityId) }

    val state by viewModel.uiState.collectAsState()
    val lastCheck by viewModel.lastCheck.collectAsState()

    if (lastCheck != null) {
        ResultScreen(viewModel = viewModel, modifier = modifier)
        return
    }

    ScalingLazyColumn(modifier = modifier.fillMaxSize()) {
        when (val current = state) {
            is UiState.Loading -> item { Text("Cargando…") }
            is UiState.Error -> item { Text(current.error.message) }
            is UiState.Success -> {
                item { ListHeader { Text(current.data.name) } }
                items(current.data.items) { item ->
                    ChecklistItemRow(
                        item = item,
                        onCheckedChange = { checked -> viewModel.toggleItem(item.id, checked) }
                    )
                }
                item {
                    PrimaryButton(
                        text = "Verificar",
                        onClick = { viewModel.verify() }
                    )
                }
            }
        }
    }
}
