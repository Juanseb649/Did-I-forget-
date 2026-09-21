package com.didiforget.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import com.didiforget.ui.theme.Dimens
import com.didiforget.ui.theme.DidIForgetOnPrimary
import com.didiforget.ui.theme.DidIForgetPrimary

/**
 * Versión compacta de [PrimaryButton]: círculo relleno `Primary` (`#329C8A`) solo
 * con ícono `OnPrimary`, del tamaño táctil mínimo (44 dp). Se usa cuando la
 * acción es evidente por el ícono ("+", "listo", "volver"). Al no llevar texto,
 * [contentDescription] es obligatorio para accesibilidad.
 */
@Composable
fun PrimaryIconButton(
    @DrawableRes icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier.size(Dimens.MinTouchTarget),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = DidIForgetPrimary,
            contentColor = DidIForgetOnPrimary
        )
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            modifier = Modifier.size(Dimens.IconMedium)
        )
    }
}
