package com.didiforget.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.didiforget.ui.theme.Dimens
import com.didiforget.ui.theme.DidIForgetOnSurface
import com.didiforget.ui.theme.DidIForgetSurface

/**
 * Chip compacto neutro (fondo `Surface`) para acciones de navegación/edición
 * secundarias, como "Inicio" o "Editar". Mantiene el `Primary` reservado para
 * la acción principal de cada pantalla.
 */
@Composable
fun SecondaryChip(
    text: String,
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier
) {
    CompactChip(
        modifier = modifier,
        onClick = onClick,
        colors = ChipDefaults.chipColors(backgroundColor = DidIForgetSurface, contentColor = DidIForgetOnSurface),
        icon = {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(Dimens.IconSmall)
            )
        },
        label = { Text(text, style = MaterialTheme.typography.caption1, color = DidIForgetOnSurface) }
    )
}
