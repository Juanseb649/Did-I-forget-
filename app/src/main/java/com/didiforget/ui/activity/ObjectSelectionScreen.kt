package com.didiforget.ui.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.didiforget.R
import com.didiforget.data.model.Activity
import com.didiforget.ui.components.IconBadge
import com.didiforget.ui.components.PrimaryButton
import com.didiforget.ui.components.SelectableItemTile
import com.didiforget.ui.icons.visualForActivity
import com.didiforget.ui.icons.visualForItem
import com.didiforget.ui.theme.Dimens
import com.didiforget.ui.theme.DidIForgetOnSurface
import com.didiforget.viewmodel.ActivityViewModel
import com.didiforget.viewmodel.UiState

/**
 * Pantalla "Selección de objetos" (docs/design/DESIGN.md, Pantalla 3): se
 * muestra después de generar la lista con IA o de agregar objetos a mano, y
 * antes de guardar. El usuario confirma en un grid de 2 columnas cuáles de
 * los objetos sugeridos realmente quiere guardar — tocar un objeto lo
 * marca/desmarca, no lo elimina de la lista.
 */
@Composable
fun ObjectSelectionScreen(
    viewModel: ActivityViewModel,
    onSaved: (Activity) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val current = state as? UiState.Success ?: return
    val activity = current.data
    val activityVisual = visualForActivity(activity.name)
    val selectedCount = activity.items.count { it.isChecked }

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
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconBadge(icon = activityVisual.icon, tint = activityVisual.tint, size = 24.dp, iconSize = Dimens.IconSmall)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = activity.name.ifBlank { stringResource(R.string.activity_new_default_name) },
                        style = MaterialTheme.typography.title2,
                        color = DidIForgetOnSurface
                    )
                }
            }
            items(activity.items.withIndex().chunked(2)) { row ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    row.forEach { (index, objectItem) ->
                        SelectableItemTile(
                            label = objectItem.name,
                            visual = visualForItem(objectItem.name),
                            selected = objectItem.isChecked,
                            onToggle = { viewModel.toggleSelection(index) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                        )
                    }
                    if (row.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            if (selectedCount > 0) {
                item {
                    PrimaryButton(
                        text = stringResource(R.string.activity_save_button_count, selectedCount),
                        icon = R.drawable.ic_check,
                        onClick = { viewModel.save(onSaved) }
                    )
                }
            }
        }
    }
}
