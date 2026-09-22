package com.didiforget.ui.checklist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.didiforget.ui.components.IconBadge
import com.didiforget.ui.components.PrimaryButton
import com.didiforget.ui.components.SecondaryIconButton
import com.didiforget.ui.icons.visualForActivity
import com.didiforget.ui.navigation.pageEnter
import com.didiforget.ui.result.ResultScreen
import com.didiforget.ui.theme.DidIForgetError
import com.didiforget.ui.theme.DidIForgetOnPrimary
import com.didiforget.ui.theme.DidIForgetOnSurface
import com.didiforget.ui.theme.DidIForgetOnSurfaceMuted
import com.didiforget.ui.theme.Dimens
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
 * "Eliminar actividad" (con confirmación) al final de la lista.
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
            modifier = Modifier.fillMaxSize().padding(horizontal = Dimens.ScreenHorizontalPadding)
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
                        val visual = visualForActivity(activity.name)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            IconBadge(icon = visual.icon, tint = visual.tint, size = 44.dp, iconSize = 26.dp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = activity.name,
                                style = MaterialTheme.typography.body2,
                                color = DidIForgetOnSurface,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    item {
                        Text(
                            text = if (editMode) {
                                stringResource(R.string.checklist_editing)
                            } else {
                                stringResource(R.string.checklist_progress, activity.checkedItems, activity.totalItems)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.caption1,
                            color = DidIForgetOnSurfaceMuted,
                            textAlign = TextAlign.Center
                        )
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
                            DestructiveButton(
                                text = stringResource(R.string.checklist_delete_activity),
                                icon = R.drawable.ic_trash,
                                onClick = { showDeleteDialog = true }
                            )
                        }
                    } else if (activity.items.isNotEmpty()) {
                        item {
                            PrimaryButton(
                                text = stringResource(R.string.checklist_verify_button),
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
