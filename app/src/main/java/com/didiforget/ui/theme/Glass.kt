package com.didiforget.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.dp

/**
 * "Liquid glass actual" (Propuesta B de la exploración de diseño): superficies
 * translúcidas y laminadas en lugar del `Surface` sólido y plano. Compose no
 * expone un backdrop-blur estable para Wear OS con minSdk 30 (requeriría
 * capturar y desenfocar lo que hay detrás, costoso en un reloj), así que el
 * vidrio se aproxima con capas: un degradado que ilumina desde arriba + un
 * borde que hace lo mismo — el mismo efecto de lectura, sin el costo.
 */
private val GlassBorderBrush = Brush.verticalGradient(listOf(GlassBorderTop, GlassBorderBottom))
private val GlassFillBrush = Brush.verticalGradient(listOf(GlassFillTop, GlassFillBottom))

/**
 * Pinta la superficie de vidrio sobre [shape]: degradado translúcido + borde
 * iluminado desde arriba. [tint] mezcla un color (de categoría o de estado)
 * encima del vidrio neutro; con `Color.Unspecified` queda neutro.
 */
fun Modifier.liquidGlass(shape: Shape, tint: Color = Color.Unspecified): Modifier =
    this
        .clip(shape)
        .background(GlassFillBrush)
        .then(if (tint.isSpecified) Modifier.background(tint) else Modifier)
        .border(BorderStroke(1.dp, GlassBorderBrush), shape)

/** Forma "squircle": esquinas redondeadas como porcentaje del propio panel, no un dp fijo. */
fun squircle(percent: Int = 32): Shape = RoundedCornerShape(percent = percent.coerceIn(0, 50))

/** Forma de píldora completa (segmentos, filas). */
val pillShape: Shape = RoundedCornerShape(percent = 50)

/**
 * Barrido de luz giratorio muy sutil (el reflejo dinámico del vidrio "actual").
 * Deliberadamente reservado para 1-2 elementos por pantalla (el botón
 * principal): es la única animación continua de la superficie de vidrio, y
 * correr muchas a la vez costaría batería sin aportar más lectura visual.
 */
@Composable
fun Modifier.glassSheen(shape: Shape): Modifier {
    val transition = rememberInfiniteTransition(label = "glassSheen")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Restart),
        label = "glassSheenAngle"
    )
    val sheenBrush = Brush.sweepGradient(
        listOf(
            Color.Transparent, GlassSheenColor, Color.Transparent,
            Color.Transparent, GlassSheenColor.copy(alpha = GlassSheenColor.alpha * 0.6f), Color.Transparent
        )
    )
    return this
        .clip(shape)
        .drawWithContent {
            drawContent()
            rotate(angle) {
                drawRect(brush = sheenBrush, blendMode = BlendMode.Screen)
            }
        }
}
