package com.didiforget.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.wear.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.didiforget.ui.theme.DidIForgetOnSurface
import com.didiforget.ui.theme.DidIForgetOnSurfaceMuted

/**
 * Pantalla de resultado a pantalla completa (DESIGN.md §4.4): un degradado
 * radial de 3 tonos, centrado hacia arriba, detrás de una insignia circular,
 * eyebrow, título, subtítulo/leyenda opcionales y un botón de cierre.
 */
@Composable
fun StatusResultScreen(
    @DrawableRes icon: Int,
    iconTint: Color,
    badgeSize: androidx.compose.ui.unit.Dp,
    iconSize: androidx.compose.ui.unit.Dp,
    badgeBackground: Color,
    glowStart: Color,
    glowMid: Color,
    glowEnd: Color,
    eyebrow: String,
    eyebrowColor: Color,
    modifier: Modifier = Modifier,
    title: String? = null,
    titleStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.title2,
    caption: String? = null,
    content: @Composable () -> Unit = {},
    footer: @Composable () -> Unit = {}
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val brush = Brush.radialGradient(
            colors = listOf(glowStart, glowMid, glowEnd),
            center = Offset(widthPx / 2f, 0f),
            radius = widthPx * 1.2f
        )
        Box(
            modifier = Modifier.fillMaxSize().background(brush),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(badgeSize)
                        .background(badgeBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(iconSize)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = eyebrow.uppercase(),
                    style = MaterialTheme.typography.caption2,
                    color = eyebrowColor,
                    textAlign = TextAlign.Center
                )
                if (title != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
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
                        text = caption,
                        style = MaterialTheme.typography.caption1,
                        color = DidIForgetOnSurfaceMuted,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                footer()
            }
        }
    }
}
