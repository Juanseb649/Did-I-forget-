package com.didiforget.viewmodel

import com.didiforget.core.DomainError

/**
 * Estado de UI genérico y exhaustivo. Cada pantalla observa un
 * `StateFlow<UiState<T>>` y decide qué componer con un `when` que el
 * compilador obliga a cubrir por completo (no hay un `else` que esconda un
 * caso olvidado).
 */
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val error: DomainError) : UiState<Nothing>()
}
