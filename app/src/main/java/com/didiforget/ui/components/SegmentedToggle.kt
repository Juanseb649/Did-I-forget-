package com.didiforget.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.wear.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.didiforget.ui.theme.DidIForgetOnPrimary
import com.didiforget.ui.theme.DidIForgetOnSurfaceMuted
import com.didiforget.ui.theme.DidIForgetPrimary
import com.didiforget.ui.theme.liquidGlass
import com.didiforget.ui.theme.pillShape

/** Una opción del [SegmentedToggle]: valor, ícono y descripción accesible (sin texto visible). */
data class ToggleOption<T>(val value: T, val label: String, @DrawableRes val icon: Int)

/**
 * Selector de dos o más opciones, solo con ícono (p. ej. "✨ generar con IA" /
 * "✏️ manual"): la pastilla seleccionada se tiñe de `Primary` sobre vidrio, la
 * otra queda en vidrio neutro. [ToggleOption.label] no se dibuja — queda como
 * descripción accesible de cada pastilla — siguiendo la exploración "Liquid
 * glass actual", que resuelve el control sin una sola palabra visible.
 */
@Composable
fun <T> SegmentedToggle(
    options: List<ToggleOption<T>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .liquidGlass(shape = pillShape)
            .padding(3.dp)
    ) {
        options.forEach { option ->
            val isSelected = option.value == selected
            val contentColor = if (isSelected) DidIForgetOnPrimary else DidIForgetOnSurfaceMuted
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .then(
                        if (isSelected) Modifier.liquidGlass(shape = pillShape, tint = DidIForgetPrimary.copy(alpha = 0.8f))
                        else Modifier
                    )
                    .clickable(role = Role.RadioButton, onClick = { onSelect(option.value) })
                    .semantics {
                        this.contentDescription = option.label
                        this.selected = isSelected
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(option.icon),
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
