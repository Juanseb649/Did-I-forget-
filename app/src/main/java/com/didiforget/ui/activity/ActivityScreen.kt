package com.didiforget.ui.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.Text
import com.didiforget.R
import com.didiforget.data.model.Activity
import com.didiforget.ui.components.PrimaryButton
import com.didiforget.viewmodel.ActivityViewModel
import com.didiforget.viewmodel.UiState

private enum class CreationMode { MANUAL, AI }

/**
 * Pantalla de creación de actividad (README, "Flujo de usuario", pasos 1-3):
 * el usuario elige entre describir la actividad para que la IA sugiera
 * objetos, o agregarlos manualmente, y luego guarda.
 */
@Composable
fun ActivityScreen(
    viewModel: ActivityViewModel,
    onSaved: (Activity) -> Unit,
    modifier: Modifier = Modifier
) {
    var mode by remember { mutableStateOf(CreationMode.AI) }
    var description by remember { mutableStateOf("") }
    var manualName by remember { mutableStateOf("") }
    var newItemName by remember { mutableStateOf("") }

    val state by viewModel.uiState.collectAsState()

    ScalingLazyColumn(modifier = modifier.fillMaxSize().padding(horizontal = 8.dp)) {
        item { ListHeader { Text(stringResource(R.string.activity_ai_tab) + " / " + stringResource(R.string.activity_manual_tab)) } }

        item {
            Chip(
                onClick = { mode = if (mode == CreationMode.AI) CreationMode.MANUAL else CreationMode.AI },
                colors = ChipDefaults.secondaryChipColors(),
                label = {
                    Text(
                        if (mode == CreationMode.AI) stringResource(R.string.activity_ai_tab)
                        else stringResource(R.string.activity_manual_tab)
                    )
                }
            )
        }

        when (mode) {
            CreationMode.AI -> {
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
                        onClick = { viewModel.generateWithAI(description) },
                        modifier = Modifier.fillMaxWidth()
                    )
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
                        placeholder = stringResource(R.string.activity_description_hint)
                    )
                }
                item {
                    SimpleTextField(
                        value = newItemName,
                        onValueChange = { newItemName = it },
                        placeholder = "Nuevo objeto…"
                    )
                }
                item {
                    PrimaryButton(
                        text = "Agregar objeto",
                        onClick = {
                            viewModel.addManualItem(newItemName)
                            newItemName = ""
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        when (val current = state) {
            is UiState.Loading -> item { Text("Generando…") }
            is UiState.Error -> item { Text(current.error.message) }
            is UiState.Success -> {
                items(current.data.items) { item -> Text("• ${item.name}") }
                if (current.data.items.isNotEmpty()) {
                    item {
                        PrimaryButton(
                            text = stringResource(R.string.activity_save_button),
                            onClick = { viewModel.save(onSaved) },
                            modifier = Modifier.fillMaxWidth()
                        )
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
            .background(Color.DarkGray)
            .padding(8.dp),
        textStyle = TextStyle(color = Color.White),
        decorationBox = { innerTextField ->
            if (value.isEmpty()) Text(placeholder, color = Color.LightGray)
            innerTextField()
        }
    )
}
