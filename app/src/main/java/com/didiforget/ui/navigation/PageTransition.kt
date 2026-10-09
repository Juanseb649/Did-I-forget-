package com.didiforget.ui.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry

private const val PAGE_ENTER_MS = 220

/** `ease-out` de CSS: cubic-bezier(0, 0, .58, 1). */
private val PageEnterEasing = CubicBezierEasing(0f, 0f, 0.58f, 1f)

/**
 * Transición de entrada de pantalla del prototipo HTML (`@keyframes enter`):
 * la pantalla aparece con fundido mientras se desliza ~16 dp desde la derecha
 * (22 px en el boceto de 450 px), 220 ms con `ease-out`. Con [enabled] = false
 * la pantalla se muestra tal cual, sin animar.
 *
 * La animación se lee dentro de `graphicsLayer`, así que corre en la fase de
 * dibujo y no provoca recomposiciones.
 */
@Composable
fun Modifier.pageEnter(enabled: Boolean = true): Modifier {
    val progress = remember { Animatable(if (enabled) 0f else 1f) }
    LaunchedEffect(Unit) {
        if (enabled) progress.animateTo(1f, tween(PAGE_ENTER_MS, easing = PageEnterEasing))
    }
    val slidePx = with(LocalDensity.current) { 16.dp.toPx() }
    return graphicsLayer {
        alpha = progress.value
        translationX = slidePx * (1f - progress.value)
    }
}

/**
 * Envuelve el contenido de un destino para darle [pageEnter] **solo la primera
 * vez** que esa entrada del back stack se muestra. Al volver atrás (con el
 * gesto de deslizar o con `popBackStack`) la pantalla anterior ya está en
 * [played] y reaparece sin animar; si no, parpadearía justo después de que el
 * gesto la dejó a la vista.
 */
@Composable
fun NavEntryPage(
    entry: NavBackStackEntry,
    played: MutableSet<String>,
    content: @Composable () -> Unit
) {
    val animate = remember { played.add(entry.id) }
    Box(modifier = Modifier.fillMaxSize().pageEnter(animate)) { content() }
}
