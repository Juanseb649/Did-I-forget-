package com.didiforget.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.wear.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.didiforget.ui.icons.ItemVisual
import com.didiforget.ui.theme.Dimens
import com.didiforget.ui.theme.DidIForgetOnSurface
import com.didiforget.ui.theme.DidIForgetOnSurfaceMuted
import com.didiforget.ui.theme.DidIForgetPrimary
import com.didiforget.ui.theme.DidIForgetPrimaryLight
import com.didiforget.ui.theme.DidIForgetPrimaryTint
import com.didiforget.ui.theme.DidIForgetSurface
import com.didiforget.ui.theme.DidIForgetSurfaceVariant

/**
 * Casilla del grid de selección de objetos (DESIGN.md §4.3): ícono + nombre,
 * con borde/fondo `Primary` tenue cuando está seleccionada, o gris plano con
 * ícono/texto atenuados cuando no (DESIGN.md §2.2).
 */
@Composable
fun SelectableItemTile(
    label: String,
    visual: ItemVisual,
    selected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) DidIForgetPrimary else DidIForgetSurfaceVariant
    val backgroundColor = if (selected) DidIForgetPrimaryTint else DidIForgetSurface
    val contentColor = if (selected) DidIForgetOnSurface else DidIForgetOnSurfaceMuted
    val iconTint = if (selected) DidIForgetPrimaryLight else DidIForgetOnSurfaceMuted

    Column(
        modifier = modifier
            .defaultMinSize(minHeight = 60.dp)
            .clickable(onClick = onToggle)
            .background(backgroundColor, RoundedCornerShape(16.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(visual.icon),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(Dimens.IconLarge)
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.body2,
            color = contentColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
