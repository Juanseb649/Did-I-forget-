package com.didiforget.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.compose.ui.semantics.Role
import com.didiforget.ui.theme.DidIForgetError
import com.didiforget.ui.theme.Dimens
import com.didiforget.ui.theme.liquidGlass
import com.didiforget.ui.theme.squircle

/**
 * Botón de acción destructiva (eliminar), convertido a vidrio solo-ícono
 * (exploración "Liquid glass actual"): mismo lenguaje visual que
 * [PrimaryIconButton] / [SecondaryIconButton], pero con tinte y borde en
 * `Error` para que siga leyéndose como una acción peligrosa pese a no tener
 * texto. [contentDescription] conserva la etiqueta para TalkBack.
 */
@Composable
fun DestructiveButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    size: Dp = Dimens.MinTouchTarget
) {
    val shape = squircle(percent = 50)
    Box(
        modifier = modifier
            .size(size)
            .liquidGlass(shape = shape, tint = DidIForgetError.copy(alpha = 0.16f))
            .border(1.dp, SolidColor(DidIForgetError.copy(alpha = 0.4f)), shape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = DidIForgetError,
            modifier = Modifier.size(size * 0.42f)
        )
    }
}
