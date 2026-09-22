package com.didiforget.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.didiforget.ui.theme.DidIForgetOnSurface
import com.didiforget.ui.theme.DidIForgetOnSurfaceMuted

/**
 * Pantalla de resultado a pantalla completa (DESIGN.md §4.4): un degradado
 * radial de 3 tonos (centrado al 38% de la altura) detrás de una insignia
 * ([badge], p. ej. [SuccessBadge] o [AlertBadge]), eyebrow, título,
 * subtítulo/leyenda opcionales y un botón de cierre. Mientras [textVisible] sea
 * `false` los textos y el botón están difuminados y transparentes; al pasar a
 * `true` se aclaran en cascada (ver [blurReveal]).
 */
@Composable
fun StatusResultScreen(
    badge: @Composable () -> Unit,
    glowStart: Color,
    glowMid: Color,
    glowEnd: Color,
    eyebrow: String,
    eyebrowColor: Color,
    modifier: Modifier = Modifier,
    eyebrowStyle: TextStyle = MaterialTheme.typography.caption2,
    title: String? = null,
    titleStyle: TextStyle = MaterialTheme.typography.title2,
    caption: String? = null,
    textVisible: Boolean = true,
    content: @Composable () -> Unit = {},
    footer: @Composable () -> Unit = {}
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }
        val brush = Brush.radialGradient(
            colors = listOf(glowStart, glowMid, glowEnd),
            center = Offset(widthPx / 2f, heightPx * 0.38f),
            radius = widthPx * 0.6f
        )
        Box(
            modifier = Modifier.fillMaxSize().background(brush),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                badge()
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    modifier = Modifier.blurReveal(textVisible),
                    text = eyebrow.uppercase(),
                    style = eyebrowStyle,
                    color = eyebrowColor,
                    textAlign = TextAlign.Center
                )
                if (title != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        modifier = Modifier.blurReveal(textVisible, delayMillis = 140),
                        text = title,
                        style = titleStyle,
                        color = DidIForgetOnSurface,
                        textAlign = TextAlign.Center
                    )
                }
                content()
                if (caption != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        modifier = Modifier.blurReveal(textVisible, delayMillis = 280),
                        text = caption,
                        style = MaterialTheme.typography.caption1,
                        color = DidIForgetOnSurfaceMuted,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier.blurReveal(textVisible, delayMillis = 400)) { footer() }
            }
        }
    }
}
