package com.didiforget.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.didiforget.R
import com.didiforget.data.model.Activity
import com.didiforget.ui.icons.visualForActivity
import com.didiforget.ui.theme.DidIForgetOnSurface
import com.didiforget.ui.theme.DidIForgetOnSurfaceDim
import com.didiforget.ui.theme.DidIForgetOnSurfaceMuted
import com.didiforget.ui.theme.DidIForgetSurface

/**
 * Representa una actividad guardada en la lista de [com.didiforget.ui.home.HomeScreen].
 * Si todavía no tiene objetos, se muestra atenuada (sin subtítulo, colores
 * más apagados) — es el estado "actividad vacía" (DESIGN.md, Pantalla 1).
 */
@Composable
fun ActivityCard(
    activity: Activity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val visual = visualForActivity(activity.name)
    val isEmpty = activity.totalItems == 0
    val labelColor = if (isEmpty) DidIForgetOnSurfaceMuted else DidIForgetOnSurface
    val iconTint = if (isEmpty) DidIForgetOnSurfaceDim else visual.tint

    Chip(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = ChipDefaults.chipColors(backgroundColor = DidIForgetSurface, contentColor = labelColor),
        icon = { IconBadge(icon = visual.icon, tint = iconTint) },
        label = { Text(activity.name, style = MaterialTheme.typography.body1, color = labelColor) },
        secondaryLabel = if (!isEmpty) {
            {
                Text(
                    pluralStringResource(
                        R.plurals.home_activity_progress,
                        activity.totalItems,
                        activity.checkedItems,
                        activity.totalItems
                    ),
                    style = MaterialTheme.typography.caption1,
                    color = DidIForgetOnSurfaceMuted
                )
            }
        } else {
            null
        }
    )
}
