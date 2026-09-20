package com.didiforget.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.wear.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.didiforget.ui.theme.DidIForgetOnPrimary
import com.didiforget.ui.theme.DidIForgetOnSurfaceMuted
import com.didiforget.ui.theme.DidIForgetPrimary
import com.didiforget.ui.theme.DidIForgetSurfaceVariant

/** Una opción del [SegmentedToggle]: valor, ícono opcional y etiqueta. */
data class ToggleOption<T>(val value: T, val label: String, @DrawableRes val icon: Int? = null)

/** Selector de dos o más opciones en forma de píldora (p. ej. "✨ IA" / "✏️ Manual"). */
@Composable
fun <T> SegmentedToggle(
    options: List<ToggleOption<T>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(DidIForgetSurfaceVariant, RoundedCornerShape(50))
            .padding(2.dp)
    ) {
        options.forEach { option ->
            val isSelected = option.value == selected
            val contentColor = if (isSelected) DidIForgetOnPrimary else DidIForgetOnSurfaceMuted
            Row(
                modifier = Modifier
                    .clickable { onSelect(option.value) }
                    .background(
                        if (isSelected) DidIForgetPrimary else Color.Transparent,
                        RoundedCornerShape(50)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (option.icon != null) {
                    Icon(
                        painter = painterResource(option.icon),
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(text = option.label, color = contentColor, style = MaterialTheme.typography.caption1)
            }
        }
    }
}
