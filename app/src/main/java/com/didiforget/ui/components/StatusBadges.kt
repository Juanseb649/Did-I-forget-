package com.didiforget.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import com.didiforget.R
import com.didiforget.ui.theme.DidIForgetAccentBright
import com.didiforget.ui.theme.DidIForgetError
import com.didiforget.ui.theme.DidIForgetErrorBadge
import kotlinx.coroutines.launch

/**
 * Insignia de "TODO LISTO", con animación de verificación en secuencia:
 * 1. el aro verde se dibuja de 0° a 360° (mientras el círculo crece de 92% a 100%
 *    y se rellena con un verde tenue),
 * 2. el check se traza sobre el aro ya cerrado,
 * 3. un pequeño rebote final asienta la insignia.
 *
 * [onCheckDrawn] se llama en cuanto el check termina de trazarse (antes del
 * rebote), para que el texto empiece a aparecer solapado con el asentamiento y
 * la secuencia se sienta continua.
 */
@Composable
fun SuccessBadge(
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    onCheckDrawn: () -> Unit = {}
) {
    val ringProgress = remember { Animatable(0f) }
    val checkProgress = remember { Animatable(0f) }
    val scale = remember { Animatable(0.92f) }
    val onCheckDrawnState by rememberUpdatedState(onCheckDrawn)

    LaunchedEffect(Unit) {
        launch { scale.animateTo(1f, tween(RING_MS, easing = FastOutSlowInEasing)) }
        ringProgress.animateTo(1f, tween(RING_MS, easing = FastOutSlowInEasing))
        checkProgress.animateTo(1f, tween(CHECK_MS, easing = FastOutSlowInEasing))
        onCheckDrawnState()
        scale.animateTo(1.07f, tween(110, easing = FastOutSlowInEasing))
        scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium))
    }

    Canvas(
        modifier = modifier
            .size(size)
            .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
    ) {
        val strokeWidth = size.toPx() * 0.075f
        val inset = strokeWidth / 2f
        val arcSize = Size(this.size.width - strokeWidth, this.size.height - strokeWidth)
        val arcTopLeft = Offset(inset, inset)

        // Relleno tenue que crece junto con el aro.
        drawCircle(
            color = DidIForgetAccentBright.copy(alpha = 0.16f * ringProgress.value),
            radius = arcSize.minDimension / 2f,
            center = center
        )
        // Aro: arranca arriba (-90°) y avanza en sentido horario.
        drawArc(
            color = DidIForgetAccentBright,
            startAngle = -90f,
            sweepAngle = 360f * ringProgress.value,
            useCenter = false,
            topLeft = arcTopLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        if (checkProgress.value > 0f) {
            val w = this.size.width
            val h = this.size.height
            val check = Path().apply {
                moveTo(w * 0.30f, h * 0.51f)
                lineTo(w * 0.44f, h * 0.65f)
                lineTo(w * 0.71f, h * 0.36f)
            }
            val partial = Path()
            val measure = PathMeasure().apply { setPath(check, false) }
            measure.getSegment(0f, measure.length * checkProgress.value, partial, true)
            drawPath(
                path = partial,
                color = DidIForgetAccentBright,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
}

private const val RING_MS = 600
private const val CHECK_MS = 380

/**
 * Alerta de "TE FALTA": círculo rojo oscuro con un triángulo de advertencia
 * coral y una onda que se expande desde el borde y se desvanece, en bucle
 * (1.6 s, `ease-out`), para llamar la atención sin depender solo de la vibración.
 */
@Composable
fun AlertBadge(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    iconSize: Dp = 32.dp,
    rippleSpread: Dp = 22.dp
) {
    val progress by rememberInfiniteTransition(label = "alertPulse").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alertPulseProgress"
    )

    Box(
        modifier = modifier
            .size(size)
            .drawBehind {
                val spread = rippleSpread.toPx() * progress
                if (spread > 0f) {
                    drawCircle(
                        color = DidIForgetError.copy(alpha = 0.55f * (1f - progress)),
                        radius = this.size.minDimension / 2f + spread / 2f,
                        style = Stroke(width = spread)
                    )
                }
            }
            .background(DidIForgetErrorBadge, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_alert_triangle),
            contentDescription = null,
            tint = DidIForgetError,
            modifier = Modifier.size(iconSize)
        )
    }
}
