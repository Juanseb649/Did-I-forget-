package com.didiforget.ui.checklist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.material.dialog.Dialog
import com.didiforget.R
import com.didiforget.ui.components.ChecklistItemRow
import com.didiforget.ui.components.DestructiveButton
import com.didiforget.ui.components.PrimaryIconButton
import com.didiforget.ui.components.ProgressRing
import com.didiforget.ui.components.ProgressRingLabel
import com.didiforget.ui.components.SecondaryIconButton
import com.didiforget.ui.navigation.pageEnter
import com.didiforget.ui.result.ResultScreen
import com.didiforget.ui.theme.DidIForgetError
import com.didiforget.ui.theme.DidIForgetOnPrimary
import com.didiforget.ui.theme.DidIForgetOnSurface
import com.didiforget.ui.theme.DidIForgetOnSurfaceMuted
import com.didiforget.ui.theme.DidIForgetPrimary
import com.didiforget.ui.theme.responsiveHorizontalPadding
import com.didiforget.ui.theme.responsiveProgressRingSize
import com.didiforget.viewmodel.ChecklistViewModel
import com.didiforget.viewmodel.UiState

/**
 * Pantalla de verificación (README, "Flujo de usuario" paso 4): el usuario
 * marca cada objeto conforme comprueba que lo lleva.
 *
 * Nota de diseño: en vez de navegar a una ruta separada para
 * [com.didiforget.ui.result.ResultScreen], esta pantalla la muestra "en
 * línea" cuando `viewModel.lastCheck` deja de ser null. `resultDismissed` es
 * estado puramente de presentación (no toca el ViewModel): permite que el
 * botón de [ResultScreen] regrese a esta lista sin depender solo del gesto
 * de swipe-to-dismiss, y se reinicia cada vez que llega una verificación
 * nueva.
 *
 * `editMode` y `showDeleteDialog` siguen el mismo criterio: son estado de
 * presentación. En modo edición cada objeto muestra una papelera y aparece
 * un botón de eliminar actividad (con confirmación) al final de la lista.
 *
 * Nota de diseño (exploración "Liquid glass actual"): el texto "X de Y
 * verificados"/"Editando" y el encabezado con el ícono y nombre de la
 * actividad se reemplazaron por un [ProgressRing] (la misma información,
 * de un vistazo) y un encabezado accesible invisible; el estado de edición
 * ahora se lee en el cambio de ícono/color del botón "Editar" en vez de un
 * rótulo de texto, igual que en el modelo.
 */
@Composable
fun ChecklistScreen(
    activityId: Long,
    viewModel: ChecklistViewModel,
    onGoHome: () -> Unit,
    onActivityDeleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(activityId) { viewModel.load(activityId) }

    val state by viewModel.uiState.collectAsState()
    val lastCheck by viewModel.lastCheck.collectAsState()
    var resultDismissed by remember(activityId) { mutableStateOf(false) }
    var editMode by remember(activityId) { mutableStateOf(false) }
    var showDeleteDialog by remember(activityId) { mutableStateOf(false) }

    LaunchedEffect(lastCheck) {
        if (lastCheck != null) resultDismissed = false
    }

    if (lastCheck != null && !resultDismissed) {
        ResultScreen(
            viewModel = viewModel,
            onBackToList = { resultDismissed = true },
            onDone = onGoHome,
            modifier = modifier.pageEnter()
        )
        return
    }

    val listState = rememberScalingLazyListState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        timeText = { TimeText() },
        positionIndicator = { PositionIndicator(scalingLazyListState = listState) }
    ) {
        ScalingLazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(horizontal = responsiveHorizontalPadding())
        ) {
            when (val current = state) {
                is UiState.Loading -> item {
                    Text(
                        text = stringResource(R.string.checklist_loading),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center
                    )
                }
                is UiState.Error -> item {
                    Text(
                        text = current.error.message,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center
                    )
                }
                is UiState.Success -> {
                    val activity = current.data
                    item {
                        // Encabezado accesible invisible: el nombre de la
                        // actividad ya no se muestra como texto, pero sigue
                        // anunciándose a TalkBack antes del aro de progreso.
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.dp)
                                .semantics(mergeDescendants = true) {
                                    heading()
                                    contentDescription = activity.name
                                }
                        )
                    }
                    item {
                        val progressDescription = if (editMode) {
                            stringResource(R.string.checklist_editing)
                        } else {
                            stringResource(R.string.checklist_progress, activity.checkedItems, activity.totalItems)
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics(mergeDescendants = true) { contentDescription = progressDescription },
                            contentAlignment = Alignment.Center
                        ) {
                            ProgressRing(
                                done = activity.checkedItems,
                                total = activity.totalItems,
                                size = responsiveProgressRingSize()
                            ) {
                                ProgressRingLabel(done = activity.checkedItems, total = activity.totalItems)
                            }
                        }
                    }
                    items(activity.items, key = { it.id }) { item ->
                        ChecklistItemRow(
                            item = item,
                            onCheckedChange = { checked -> viewModel.toggleItem(item.id, checked) },
                            onDelete = if (editMode) ({ viewModel.deleteItem(item) }) else null
                        )
                    }
                    if (activity.items.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.checklist_empty),
                                modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.body2,
                                color = DidIForgetOnSurfaceMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    if (editMode) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                DestructiveButton(
                                    icon = R.drawable.ic_trash,
                                    contentDescription = stringResource(R.string.checklist_delete_activity),
                                    onClick = { showDeleteDialog = true }
                                )
                            }
                        }
                    } else if (activity.items.isNotEmpty()) {
                        item {
                            PrimaryIconButton(
                                icon = R.drawable.ic_check,
                                contentDescription = stringResource(R.string.checklist_verify_button),
                                onClick = { viewModel.verify() }
                            )
                        }
                    }
                }
            }

            // Acciones secundarias: pequeñas y debajo de "Verificar" para que éste resalte.
            item {
                val canEdit = state is UiState.Success
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SecondaryIconButton(
                        icon = R.drawable.ic_chevron_left,
                        contentDescription = stringResource(R.string.checklist_home),
                        onClick = onGoHome
                    )
                    if (canEdit) {
                        SecondaryIconButton(
                            icon = if (editMode) R.drawable.ic_check else R.drawable.ic_pencil,
                            contentDescription = stringResource(
                                if (editMode) R.string.checklist_done_editing else R.string.checklist_edit
                            ),
                            tint = if (editMode) DidIForgetPrimary else DidIForgetOnSurface,
                            onClick = { editMode = !editMode }
                        )
                    }
                }
            }
        }
    }

    val activityName = (state as? UiState.Success)?.data?.name.orEmpty()
    Dialog(showDialog = showDeleteDialog, onDismissRequest = { showDeleteDialog = false }) {
        Alert(
            title = {
                Text(
                    text = stringResource(R.string.checklist_delete_confirm_title, activityName),
                    textAlign = TextAlign.Center,
                    color = DidIForgetOnSurface
                )
            },
            negativeButton = {
                Button(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.secondaryButtonColors()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_x),
                        contentDescription = stringResource(R.string.common_cancel)
                    )
                }
            },
            positiveButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteActivity(onActivityDeleted)
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = DidIForgetError,
                        contentColor = DidIForgetOnPrimary
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_trash),
                        contentDescription = stringResource(R.string.checklist_delete_confirm)
                    )
                }
            }
        ) {
            Text(
                text = stringResource(R.string.checklist_delete_confirm_message),
                style = MaterialTheme.typography.body2,
                color = DidIForgetOnSurfaceMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}
