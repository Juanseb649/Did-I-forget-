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
import com.didiforget.ui.theme.DidIForgetPrimary
import com.didiforget.ui.theme.DidIForgetPrimaryTint
import com.didiforget.ui.theme.DidIForgetSurface

private val CheckboxBorder = Color(0xFF3A5257)

/**
 * Fila de la checklist (DESIGN.md §4.2): ícono del objeto + nombre + círculo de verificación.
 * Si [onDelete] no es null (modo edición), el círculo se reemplaza por una
 * papelera y tocar la fila elimina el objeto en vez de marcarlo.
 */
@Composable
fun ChecklistItemRow(
    item: Item,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onDelete: (() -> Unit)? = null
) {
    val visual = visualForItem(item.name)
    val backgroundColor = if (item.isChecked && onDelete == null) DidIForgetPrimaryTint else DidIForgetSurface
    val interaction = if (onDelete != null) {
        Modifier.clickable(role = Role.Button, onClick = onDelete)
    } else {
        Modifier.toggleable(value = item.isChecked, role = Role.Checkbox, onValueChange = onCheckedChange)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = Dimens.MinTouchTarget)
            .then(interaction)
            .background(backgroundColor, RoundedCornerShape(50))
            .padding(vertical = 9.dp, horizontal = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(visual.icon),
            contentDescription = null,
            tint = visual.tint,
            modifier = Modifier.size(Dimens.IconMedium)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = item.name,
            style = MaterialTheme.typography.body1,
            color = DidIForgetOnSurface,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        if (onDelete != null) {
            Icon(
                painter = painterResource(R.drawable.ic_trash),
                contentDescription = stringResource(R.string.content_delete_item, item.name),
                tint = DidIForgetError,
                modifier = Modifier.size(Dimens.IconMedium)
            )
        } else if (item.isChecked) {
            Box(
                modifier = Modifier.size(21.dp).background(DidIForgetPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    tint = DidIForgetOnPrimary,
                    modifier = Modifier.size(13.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(21.dp)
                    .border(2.dp, CheckboxBorder, CircleShape)
            )
        }
    }
}
