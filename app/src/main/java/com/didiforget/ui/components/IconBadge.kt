package com.didiforget.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.wear.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.didiforget.ui.theme.liquidGlass
import com.didiforget.ui.theme.squircle

/**
 * Insignia "squircle" de vidrio con un ícono vectorial (Tabler), usada como
 * ícono de actividad/objeto. El fondo es vidrio (ver [liquidGlass]) teñido con
 * el `tint` al 16% de alfa; el ícono usa el `tint` completo — así las
 * categorías se distinguen sin competir con el único color de acento de los
 * botones (ver DESIGN.md §2.2 y la exploración "Liquid glass actual").
 */
@Composable
fun IconBadge(
    @DrawableRes icon: Int,
    tint: Color,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    iconSize: Dp = 18.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .liquidGlass(shape = squircle(), tint = tint.copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}
