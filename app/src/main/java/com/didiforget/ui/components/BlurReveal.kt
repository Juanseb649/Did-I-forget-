package com.didiforget.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Hace que un elemento pase de difuminado y transparente a nítido y opaco
 * cuando [visible] pasa a `true` (tras [delayMillis]). Si ya nace visible no
 * anima nada. El elemento conserva su espacio en el layout mientras está oculto,
 * así que no hay saltos. El desenfoque solo se aplica en Android 12+ (API 31);
 * en versiones anteriores queda el fundido de opacidad.
 */
@Composable
fun Modifier.blurReveal(
    visible: Boolean,
    delayMillis: Int = 0,
    durationMillis: Int = 650,
    maxBlur: Dp = 10.dp
): Modifier {
    val progress by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis, delayMillis, FastOutSlowInEasing),
        label = "blurReveal"
    )
    return this
        .graphicsLayer { alpha = progress }
        .then(
            if (progress < 1f) Modifier.blur(maxBlur * (1f - progress), BlurredEdgeTreatment.Unbounded)
            else Modifier
        )
}
