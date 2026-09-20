package com.didiforget.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.didiforget.data.model.Activity
import com.didiforget.ui.icons.visualForActivity
import com.didiforget.ui.theme.Dimens
import com.didiforget.ui.theme.DidIForgetOnSurface
import com.didiforget.ui.theme.DidIForgetOnSurfaceDim
import com.didiforget.ui.theme.DidIForgetOnSurfaceMuted
import com.didiforget.ui.theme.DidIForgetSurface

/**
 * Casilla de actividad de la cuadrícula de [com.didiforget.ui.home.HomeScreen]:
 * ícono arriba y nombre abajo, sobre fondo `Surface`. Es lo bastante grande para
 * tocarla con comodidad en el reloj sin ocupar todo el ancho. Si la actividad
 * todavía no tiene objetos se muestra atenuada (DESIGN.md, Pantalla 1).
 */
@Composable
fun ActivityTile(
    activity: Activity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val visual = visualForActivity(activity.name)
    val isEmpty = activity.totalItems == 0
    val labelColor = if (isEmpty) DidIForgetOnSurfaceMuted else DidIForgetOnSurface
    val iconTint = if (isEmpty) DidIForgetOnSurfaceDim else visual.tint
    val shape = RoundedCornerShape(24.dp)

    Column(
        modifier = modifier
            .height(Dimens.ActivityTileHeight)
            .clip(shape)
            .background(DidIForgetSurface, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
    ) {
        Icon(
            painter = painterResource(visual.icon),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(Dimens.IconLarge)
        )
        Text(
            text = activity.name,
            style = MaterialTheme.typography.body2,
            color = labelColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
