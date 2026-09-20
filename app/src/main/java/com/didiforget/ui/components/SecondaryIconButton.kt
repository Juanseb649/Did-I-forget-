package com.didiforget.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import com.didiforget.ui.theme.Dimens
import com.didiforget.ui.theme.DidIForgetOnSurface
import com.didiforget.ui.theme.DidIForgetSurface

/**
 * Botón circular pequeño y neutro (fondo `Surface`) para acciones de
 * navegación/edición secundarias, como "Inicio" o "Editar". Es deliberadamente
 * más pequeño que [PrimaryButton] para que la acción principal de la pantalla
 * siga siendo la que resalta. Solo lleva ícono, así que [contentDescription]
 * es obligatorio para accesibilidad.
 */
@Composable
fun SecondaryIconButton(
    @DrawableRes icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier.size(36.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = DidIForgetSurface,
            contentColor = DidIForgetOnSurface
        )
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            modifier = Modifier.size(Dimens.IconSmall)
        )
    }
}
