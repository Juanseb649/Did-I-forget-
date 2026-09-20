package com.didiforget.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.didiforget.core.DomainError
import com.didiforget.data.model.Activity
import com.didiforget.data.model.CheckHistory
import com.didiforget.data.model.Item
import com.didiforget.data.repository.ActivityRepository
import com.didiforget.domain.usecase.DeleteActivityUseCase
import com.didiforget.domain.usecase.DeleteItemUseCase
import com.didiforget.domain.usecase.ToggleItemUseCase
import com.didiforget.domain.usecase.VerifyChecklistUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado de [com.didiforget.ui.checklist.ChecklistScreen] y
 * [com.didiforget.ui.result.ResultScreen]: cargar una actividad, marcar
 * ítems, y verificar si falta algo antes de salir.
 */
class ChecklistViewModel(
    private val activityRepository: ActivityRepository,
    private val toggleItemUseCase: ToggleItemUseCase,
    private val verifyChecklistUseCase: VerifyChecklistUseCase,
    private val deleteItemUseCase: DeleteItemUseCase,
    private val deleteActivityUseCase: DeleteActivityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Activity>>(UiState.Loading)
    val uiState: StateFlow<UiState<Activity>> = _uiState.asStateFlow()

    private val _lastCheck = MutableStateFlow<CheckHistory?>(null)
    val lastCheck: StateFlow<CheckHistory?> = _lastCheck.asStateFlow()

    fun load(activityId: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val activity = activityRepository.getActivity(activityId)
            _uiState.value = if (activity != null) {
                UiState.Success(activity)
            } else {
                UiState.Error(DomainError.ActivityNotFound)
            }
        }
    }

    fun toggleItem(itemId: Long, checked: Boolean) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        // Actualización optimista: la UI responde de inmediato...
        val updated = current.copy(
            items = current.items.map { if (it.id == itemId) it.copy(isChecked = checked) else it }
        )
        _uiState.value = UiState.Success(updated)

        // ...y se confirma en segundo plano contra Room.
        viewModelScope.launch { toggleItemUseCase(itemId, checked) }
    }

    fun deleteItem(item: Item) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        // Igual que toggleItem: la UI se actualiza de inmediato y Room se confirma después.
        _uiState.value = UiState.Success(current.copy(items = current.items.filterNot { it.id == item.id }))
        viewModelScope.launch { deleteItemUseCase(item) }
    }

    fun deleteActivity(onDeleted: () -> Unit) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            deleteActivityUseCase(current.id)
            onDeleted()
        }
    }

    /** Corresponde al paso "¿Falta algo?" del flujo de la app. */
    fun verify() {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            _lastCheck.value = verifyChecklistUseCase(current)
        }
    }
}
