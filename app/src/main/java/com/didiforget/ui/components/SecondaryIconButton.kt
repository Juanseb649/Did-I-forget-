package com.didiforget.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import com.didiforget.ui.theme.DidIForgetOnSurface
import com.didiforget.ui.theme.Dimens
import com.didiforget.ui.theme.liquidGlass
import com.didiforget.ui.theme.squircle

/**
 * Botón circular pequeño y neutro, de vidrio pero sin teñir, para acciones de
 * navegación/edición secundarias, como "Inicio" o "Editar". Deliberadamente
 * más pequeño y apagado que [PrimaryIconButton] para que la acción principal
 * de la pantalla siga siendo la que resalta. Solo lleva ícono, así que
 * [contentDescription] es obligatorio para accesibilidad.
 */
@Composable
fun SecondaryIconButton(
    @DrawableRes icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: androidx.compose.ui.graphics.Color = DidIForgetOnSurface
) {
    val shape = squircle(percent = 50)
    Box(
        modifier = modifier
            .size(36.dp)
            .liquidGlass(shape = shape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(Dimens.IconSmall)
        )
    }
}
