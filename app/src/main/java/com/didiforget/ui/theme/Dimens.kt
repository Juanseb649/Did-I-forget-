package com.didiforget.ui.theme

import androidx.compose.ui.unit.dp

/** Medidas compartidas para que listas, botones e íconos se vean uniformes en todas las pantallas. */
object Dimens {
    /** Margen horizontal de las listas (ScalingLazyColumn) en pantallas redondas. */
    val ScreenHorizontalPadding = 40.dp

    /** Margen horizontal de la cuadrícula de actividades del Home (más ancha que las listas). */
    val ActivityGridHorizontalPadding = 20.dp

    /** Separación entre casillas de la cuadrícula de actividades. */
    val ActivityGridSpacing = 8.dp

    /** Alto de cada casilla de actividad: cómodo de tocar, sin ser gigante. */
    val ActivityTileHeight = 72.dp

    /** Alto táctil mínimo de chips y botones. */
    val MinTouchTarget = 44.dp

    val IconSmall = 16.dp
    val IconMedium = 20.dp
    val IconLarge = 24.dp
}
