package com.didiforget.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Text
import com.didiforget.data.model.Activity

/**
 * Representa una actividad guardada en la lista de [com.didiforget.ui.home.HomeScreen].
 * Ejemplo visual (ver README): "🎓 Universidad — 6 objetos".
 */
@Composable
fun ActivityCard(
    activity: Activity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Chip(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = ChipDefaults.secondaryChipColors(),
        label = { Text(activity.name) },
        secondaryLabel = {
            Text("${activity.checkedItems}/${activity.totalItems} objetos")
        }
    )
}
