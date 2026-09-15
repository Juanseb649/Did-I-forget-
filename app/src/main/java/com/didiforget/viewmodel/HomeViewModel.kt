package com.didiforget.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.didiforget.core.DomainError
import com.didiforget.data.model.Activity
import com.didiforget.data.repository.ActivityRepository
import com.didiforget.domain.usecase.DeleteActivityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * Estado y lógica de presentación de [com.didiforget.ui.home.HomeScreen].
 * No conoce Room ni Compose: solo la interfaz [ActivityRepository] y los
 * Use Cases del dominio.
 */
class HomeViewModel(
    private val activityRepository: ActivityRepository,
    private val deleteActivityUseCase: DeleteActivityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Activity>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Activity>>> = _uiState.asStateFlow()

    init {
        observeActivities()
    }

    private fun observeActivities() {
        viewModelScope.launch {
            activityRepository.observeActivities()
                .catch { _uiState.value = UiState.Error(DomainError.Unknown(it.message ?: "")) }
                .collect { activities -> _uiState.value = UiState.Success(activities) }
        }
    }

    fun deleteActivity(id: Long) {
        viewModelScope.launch { deleteActivityUseCase(id) }
    }
}
