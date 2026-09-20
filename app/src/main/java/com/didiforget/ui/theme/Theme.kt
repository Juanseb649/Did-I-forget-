package com.didiforget.ui.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

private val DidIForgetColors = Colors(
    primary = DidIForgetPrimary,
    primaryVariant = DidIForgetPrimaryVariant,
    secondary = DidIForgetSecondary,
    error = DidIForgetError,
    background = DidIForgetBackground,
    surface = DidIForgetSurface,
    onPrimary = DidIForgetOnPrimary,
    onSurface = DidIForgetOnSurface
)

@Composable
fun DidIForgetTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DidIForgetColors,
        typography = DidIForgetTypography,
        content = content
    )
}
