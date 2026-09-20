package com.didiforget.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.didiforget.ui.theme.Dimens
import com.didiforget.ui.theme.DidIForgetError

/**
 * Botón para acciones destructivas (eliminar). Es la única excepción a la
 * regla "una sola variante de botón" (DESIGN.md): sin relleno, con borde y
 * contenido en `Error`, para que no compita con [PrimaryButton].
 */
@Composable
fun DestructiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int? = null
) {
    Chip(
        modifier = modifier.fillMaxWidth().heightIn(min = Dimens.MinTouchTarget),
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ChipDefaults.outlinedChipColors(contentColor = DidIForgetError),
        border = ChipDefaults.outlinedChipBorder(borderColor = DidIForgetError),
        label = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = DidIForgetError,
                        modifier = Modifier.size(Dimens.IconMedium)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(text, style = MaterialTheme.typography.button, color = DidIForgetError)
            }
        }
    )
}
