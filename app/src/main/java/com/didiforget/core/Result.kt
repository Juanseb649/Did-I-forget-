package com.didiforget.core

/**
 * Envoltorio explícito para operaciones que pueden fallar (por ejemplo, una
 * llamada al [com.didiforget.ai.AIService]).
 *
 * Se usa en vez de lanzar excepciones hacia arriba porque:
 *  - El compilador obliga a manejar ambos casos en un `when` exhaustivo.
 *  - El error queda tipado ([DomainError]) en vez de ser un `Throwable` genérico.
 *  - El ViewModel puede mapear directamente a [com.didiforget.viewmodel.UiState]
 *    sin try/catch disperso por toda la app.
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val error: DomainError) : Result<Nothing>()

    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Failure -> this
    }

    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onFailure(action: (DomainError) -> Unit): Result<T> {
        if (this is Failure) action(error)
        return this
    }
}

/** Errores de negocio conocidos por la app (no excepciones técnicas crudas). */
sealed class DomainError(val message: String) {
    data object AiUnavailable : DomainError("No se pudo generar la lista con IA.")
    data object EmptyDescription : DomainError("Describe la actividad antes de generar la lista.")
    data object ActivityNotFound : DomainError("La actividad ya no existe.")
    data class Unknown(val cause: String) : DomainError(cause)
}
