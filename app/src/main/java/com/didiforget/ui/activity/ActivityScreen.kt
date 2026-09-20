package com.didiforget.ui.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.wear.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.didiforget.R
import com.didiforget.ui.components.PrimaryButton
import com.didiforget.ui.components.SegmentedToggle
import com.didiforget.ui.components.ToggleOption
import com.didiforget.ui.theme.Dimens
import com.didiforget.ui.theme.DidIForgetOnSurface
import com.didiforget.ui.theme.DidIForgetOnSurfaceMuted
import com.didiforget.ui.theme.DidIForgetSurfaceVariant
import com.didiforget.viewmodel.ActivityViewModel
import com.didiforget.viewmodel.UiState

private enum class CreationMode { AI, MANUAL }

/**
 * Pantalla de creación de actividad (README, "Flujo de usuario", pasos 1-2):
 * el usuario elige entre describir la actividad para que la IA sugiera
 * objetos, o agregarlos manualmente; una vez hay objetos, continúa a la
 * grilla de selección ([com.didiforget.ui.activity.ObjectSelectionScreen])
 * donde elige cuáles de esas sugerencias realmente quiere guardar.
 */
@Composable
fun ActivityScreen(
    viewModel: ActivityViewModel,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    var mode by remember { mutableStateOf(CreationMode.AI) }
    var description by remember { mutableStateOf("") }
    var manualName by remember { mutableStateOf("") }
    var newItemName by remember { mutableStateOf("") }

    val state by viewModel.uiState.collectAsState()
    val hasItems = (state as? UiState.Success)?.data?.items?.isNotEmpty() == true
    // En modo IA, una vez generada la lista se muestra solo el botón para
    // continuar a la grilla de selección; en modo manual el usuario sigue
    // viendo los campos para seguir agregando objetos.
    val showInputSection = mode == CreationMode.MANUAL || !hasItems

    val listState = rememberScalingLazyListState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        timeText = { TimeText() },
        positionIndicator = { PositionIndicator(scalingLazyListState = listState) }
    ) {
        ScalingLazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(horizontal = Dimens.ScreenHorizontalPadding)
        ) {
            if (showInputSection) {
                item {
                    SegmentedToggle(
                        options = listOf(
                            ToggleOption(CreationMode.AI, stringResource(R.string.activity_ai_tab), R.drawable.ic_sparkles),
                            ToggleOption(CreationMode.MANUAL, stringResource(R.string.activity_manual_tab), R.drawable.ic_pencil)
                        ),
                        selected = mode,
                        onSelect = { mode = it }
                    )
                }

                when (mode) {
                    CreationMode.AI -> {
                        item {
                            Text(
                                text = stringResource(R.string.activity_description_label).uppercase(),
                                style = MaterialTheme.typography.caption2,
                                color = DidIForgetOnSurfaceMuted
                            )
                        }
                        item {
                            SimpleTextField(
                                value = description,
                                onValueChange = { description = it },
                                placeholder = stringResource(R.string.activity_description_hint)
                            )
                        }
                        item {
                            PrimaryButton(
                                text = stringResource(R.string.activity_generate_button),
                                icon = R.drawable.ic_sparkles,
                                onClick = { viewModel.generateWithAI(description) }
                            )
                        }
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_microphone),
                                    contentDescription = null,
                                    tint = DidIForgetOnSurfaceMuted,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(R.string.activity_voice_hint),
                                    style = MaterialTheme.typography.caption1,
                                    color = DidIForgetOnSurfaceMuted
                                )
                            }
                        }
                    }

                    CreationMode.MANUAL -> {
                        item {
                            SimpleTextField(
                                value = manualName,
                                onValueChange = {
                                    manualName = it
                                    viewModel.startManualActivity(it)
                                },
                                placeholder = stringResource(R.string.activity_name_hint)
                            )
                        }
                        item {
                            SimpleTextField(
                                value = newItemName,
                                onValueChange = { newItemName = it },
                                placeholder = stringResource(R.string.activity_new_item_hint)
                            )
                        }
                        item {
                            PrimaryButton(
                                text = stringResource(R.string.activity_add_item_button),
                                onClick = {
                                    viewModel.addManualItem(newItemName)
                                    newItemName = ""
                                }
                            )
                        }
                    }
                }
            }

            when (val current = state) {
                is UiState.Loading -> item {
                    Text(
                        text = stringResource(R.string.activity_generating),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.body2,
                        color = DidIForgetOnSurfaceMuted,
                        textAlign = TextAlign.Center
                    )
                }
                is UiState.Error -> item {
                    Text(
                        text = current.error.message,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center
                    )
                }
                is UiState.Success -> {
                    if (current.data.items.isNotEmpty()) {
                        item {
                            PrimaryButton(
                                text = stringResource(
                                    R.string.activity_review_button,
                                    current.data.items.size
                                ),
                                onClick = onContinue
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Campo de texto mínimo para Wear OS. `androidx.wear.compose.material` no
 * incluye un `TextField` propio, así que se usa `BasicTextField` (de Compose
 * Foundation) con un estilo simple. En un reloj real, el sistema abre su
 * panel de entrada (voz o teclado) al enfocar el campo.
 */
@Composable
private fun SimpleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .background(DidIForgetSurfaceVariant, RoundedCornerShape(18.dp))
            .padding(10.dp),
        textStyle = TextStyle(color = DidIForgetOnSurface, fontSize = 15.sp),
        cursorBrush = SolidColor(DidIForgetOnSurface),
        decorationBox = { innerTextField ->
            if (value.isEmpty()) {
                Text(placeholder, color = DidIForgetOnSurfaceMuted, style = MaterialTheme.typography.body2)
            }
            innerTextField()
        }
    )
}
