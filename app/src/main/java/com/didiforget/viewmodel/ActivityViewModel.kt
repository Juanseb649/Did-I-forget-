package com.didiforget.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.didiforget.core.Result
import com.didiforget.data.model.Activity
import com.didiforget.data.model.Item
import com.didiforget.data.model.ItemSource
import com.didiforget.domain.usecase.GenerateChecklistUseCase
import com.didiforget.domain.usecase.SaveActivityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado y lógica de [com.didiforget.ui.activity.ActivityScreen]: crear una
 * actividad de forma manual o generar su checklist con IA, y guardarla.
 */
class ActivityViewModel(
    private val generateChecklistUseCase: GenerateChecklistUseCase,
    private val saveActivityUseCase: SaveActivityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Activity>>(
        UiState.Success(Activity(name = "", items = emptyList()))
    )
    val uiState: StateFlow<UiState<Activity>> = _uiState.asStateFlow()

    /** El usuario escribe una descripción y pide que la IA sugiera objetos. */
    fun generateWithAI(description: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = generateChecklistUseCase(description)) {
                is Result.Success -> _uiState.value = UiState.Success(result.data)
                is Result.Failure -> _uiState.value = UiState.Error(result.error)
            }
        }
    }

    /** El usuario crea/edita la actividad manualmente, sin IA. */
    fun startManualActivity(name: String) {
        _uiState.value = UiState.Success(Activity(name = name, items = emptyList()))
    }

    fun addManualItem(name: String) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        if (name.isBlank()) return
        val newItem = Item(activityId = current.id, name = name.trim(), source = ItemSource.MANUAL)
        _uiState.value = UiState.Success(current.copy(items = current.items + newItem))
    }

    fun removeItem(item: Item) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.value = UiState.Success(current.copy(items = current.items - item))
    }

    fun save(onSaved: (Activity) -> Unit) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            val saved = saveActivityUseCase(current)
            _uiState.value = UiState.Success(saved)
            onSaved(saved)
        }
    }
}
