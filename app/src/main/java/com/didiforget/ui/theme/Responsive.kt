package com.didiforget.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Tamaños que escalan con el diámetro físico de la pantalla redonda del
 * reloj, en vez de quedar fijos a un solo modelo (ver exploración de diseño
 * "Liquid glass actual"). Un Pixel Watch pequeño (~380 dp) y un Galaxy Watch
 * grande (~480 dp) terminan con la misma PROPORCIÓN de margen, grilla e
 * íconos — no con el mismo valor absoluto.
 *
 * `MinTouchTarget` (44 dp) nunca se escala hacia abajo: es un piso de
 * accesibilidad, no una preferencia visual, así que cada función aquí lo usa
 * como mínimo con `maxOf`.
 */
@Composable
fun rememberScreenDiameter(): Dp = LocalConfiguration.current.screenWidthDp.dp

/** Margen horizontal de listas y pantallas de un solo flujo. */
@Composable
fun responsiveHorizontalPadding(): Dp = rememberScreenDiameter() * 0.09f

/** Margen horizontal de la cuadrícula de actividades (más angosto que el de listas). */
@Composable
fun responsiveGridPadding(): Dp = rememberScreenDiameter() * 0.045f

/** Alto de cada casilla de actividad/objeto en una cuadrícula. */
@Composable
fun responsiveTileHeight(): Dp = rememberScreenDiameter() * 0.17f

/** Diámetro de un botón flotante principal (el único elemento "grande" de cada pantalla). */
@Composable
fun responsiveFabSize(): Dp = maxOf(rememberScreenDiameter() * 0.22f, Dimens.MinTouchTarget)

/** Tamaño de la insignia principal de una pantalla. */
@Composable
fun responsiveBadgeSize(): Dp = rememberScreenDiameter() * 0.16f

/** Diámetro del aro de progreso de Checklist (reemplaza el texto "X de Y verificados"). */
@Composable
fun responsiveProgressRingSize(): Dp = rememberScreenDiameter() * 0.22f
