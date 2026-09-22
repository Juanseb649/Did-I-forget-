package com.didiforget.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.wear.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.didiforget.R
import com.didiforget.data.model.Item
import com.didiforget.ui.icons.visualForItem
import com.didiforget.ui.theme.Dimens
import com.didiforget.ui.theme.DidIForgetError
import com.didiforget.ui.theme.DidIForgetOnPrimary
import com.didiforget.ui.theme.DidIForgetOnSurface
import com.didiforget.ui.theme.DidIForgetOnSurfaceMuted
import com.didiforget.ui.theme.DidIForgetPrimary
import com.didiforget.ui.theme.DidIForgetPrimaryTint
import com.didiforget.ui.theme.DidIForgetSurface

private val CheckboxBorder = Color(0xFF3A5257)

/**
 * Fila de la checklist (boceto HTML `.item-row`): ícono del objeto + nombre +
 * círculo de verificación, en una tarjeta de esquinas redondeadas. Marcada, la
 * tarjeta se tiñe de `Primary` y el ícono pasa a `Primary`; sin marcar, el
 * ícono va en gris. Si [onDelete] no es null (modo edición), la fila deja de
 * poder marcarse y aparece a su derecha un botón circular de papelera que
 * elimina el objeto.
 */
@Composable
fun ChecklistItemRow(
    item: Item,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onDelete: (() -> Unit)? = null
) {
    val visual = visualForItem(item.name)
    val editing = onDelete != null
    val checked = item.isChecked && !editing
    val shape = RoundedCornerShape(16.dp)

    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Row(
            modifier = Modifier
                .weight(1f)
                .defaultMinSize(minHeight = Dimens.MinTouchTarget)
                .then(
                    if (editing) Modifier
                    else Modifier.toggleable(value = item.isChecked, role = Role.Checkbox, onValueChange = onCheckedChange)
                )
                .background(if (checked) DidIForgetPrimaryTint else DidIForgetSurface, shape)
                .padding(vertical = 10.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(visual.icon),
                contentDescription = null,
                tint = if (checked) DidIForgetPrimary else DidIForgetOnSurfaceMuted,
                modifier = Modifier.size(Dimens.IconMedium)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = item.name,
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Medium),
                color = DidIForgetOnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (!editing) {
                Spacer(modifier = Modifier.width(8.dp))
                if (checked) {
                    Box(
                        modifier = Modifier.size(24.dp).background(DidIForgetPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_check),
                            contentDescription = null,
                            tint = DidIForgetOnPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                } else {
                    Box(modifier = Modifier.size(24.dp).border(2.dp, CheckboxBorder, CircleShape))
                }
            }
        }
        if (editing) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(DidIForgetError.copy(alpha = 0.14f), CircleShape)
                    .clickable(role = Role.Button, onClick = onDelete),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_trash),
                    contentDescription = stringResource(R.string.content_delete_item, item.name),
                    tint = DidIForgetError,
                    modifier = Modifier.size(Dimens.IconSmall)
                )
            }
        }
    }
}
