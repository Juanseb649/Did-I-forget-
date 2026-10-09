package com.didiforget.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.didiforget.ui.theme.DidIForgetOnPrimary
import com.didiforget.ui.theme.DidIForgetPrimary
import com.didiforget.ui.theme.DidIForgetSecondary
import com.didiforget.ui.theme.glassSheen
import com.didiforget.ui.theme.liquidGlass
import com.didiforget.ui.theme.responsiveFabSize
import com.didiforget.ui.theme.squircle

/**
 * Botón de acción principal de cada pantalla: círculo de vidrio teñido de
 * `Primary`, solo con ícono (al no llevar texto, [contentDescription] es
 * obligatorio para accesibilidad). Respira con un brillo que crece y se
 * desvanece — la animación "breathe" de la exploración de diseño — para que
 * siga leyéndose como LA acción de la pantalla sin necesitar una etiqueta.
 * [badgeCount], si no es null, dibuja una insignia con el número encima
 * (p. ej. cuántos objetos quedaron marcados antes de guardar).
 */
@Composable
fun PrimaryIconButton(
    @DrawableRes icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeCount: Int? = null,
    size: Dp = responsiveFabSize()
) {
    val breathe = rememberInfiniteTransition(label = "primaryButtonBreathe")
    val glow by breathe.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "primaryButtonGlow"
    )
    val shape = squircle(percent = 50) // círculo: el FAB es el único elemento totalmente redondo

    Box(contentAlignment = Alignment.TopEnd) {
        Box(
            modifier = modifier
                .size(size)
                .drawBehind {
                    val stroke = 2.dp.toPx() + (4.dp.toPx() * glow)
                    drawCircle(
                        color = DidIForgetPrimary.copy(alpha = 0.35f * (1f - glow)),
                        radius = (this.size.minDimension / 2f) + stroke,
                        style = Stroke(width = stroke)
                    )
                }
                .liquidGlass(shape = shape, tint = DidIForgetPrimary.copy(alpha = 0.55f))
                .glassSheen(shape)
                .clickable(role = Role.Button, onClick = onClick)
                .semantics { this.contentDescription = contentDescription },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = DidIForgetOnPrimary,
                modifier = Modifier.size(size * 0.42f)
            )
        }
        if (badgeCount != null && badgeCount > 0) {
            Box(
                modifier = Modifier
                    .offset(x = 4.dp, y = (-4).dp)
                    .size(size * 0.34f)
                    .background(DidIForgetSecondary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badgeCount.toString(),
                    style = MaterialTheme.typography.caption2,
                    color = DidIForgetOnPrimary
                )
            }
        }
    }
}
