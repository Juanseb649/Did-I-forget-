package com.didiforget.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.didiforget.ui.theme.DidIForgetOnSurface

/**
 * Reemplaza el texto "X de Y verificados" por un aro de progreso alrededor de
 * un contenido central (normalmente un [IconBadge]): la misma información,
 * leída de un vistazo en vez de un renglón de texto (exploración "Liquid
 * glass actual"). El número en el centro se mantiene — es un dato, no un
 * título — para quien prefiera confirmarlo con precisión.
 */
@Composable
fun ProgressRing(
    done: Int,
    total: Int,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
    strokeWidth: Dp = 4.dp,
    trackColor: Color = Color.White.copy(alpha = 0.12f),
    progressColor: Color = DidIForgetOnSurface,
    content: @Composable () -> Unit = {}
) {
    val ratio = if (total > 0) done.toFloat() / total else 0f
    val progress by animateFloatAsState(
        targetValue = ratio,
        animationSpec = tween(360, easing = FastOutSlowInEasing),
        label = "checklistProgressRing"
    )

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = strokeWidth.toPx()
            val inset = stroke / 2f
            val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
            val topLeft = Offset(inset, inset)
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            if (progress > 0f) {
                drawArc(
                    color = progressColor,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }
        }
        content()
    }
}

/** Número compacto "hecho/total" para el centro de un [ProgressRing]. */
@Composable
fun ProgressRingLabel(done: Int, total: Int, modifier: Modifier = Modifier) {
    Text(
        text = "$done/$total",
        style = MaterialTheme.typography.caption1,
        color = DidIForgetOnSurface,
        modifier = modifier
    )
}
