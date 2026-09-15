package com.didiforget.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.ToggleChipDefaults
import com.didiforget.data.model.Item

/** Fila de la checklist: "☑ Computador" / "☐ Computador". */
@Composable
fun ChecklistItemRow(
    item: Item,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    ToggleChip(
        modifier = modifier.fillMaxWidth(),
        checked = item.isChecked,
        onCheckedChange = onCheckedChange,
        label = { Text(item.name) },
        toggleControl = {
            androidx.wear.compose.material.Checkbox(
                checked = item.isChecked,
                enabled = true
            )
        },
        colors = ToggleChipDefaults.toggleChipColors()
    )
}
