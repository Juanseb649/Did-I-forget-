package com.didiforget.ai

import com.didiforget.core.DomainError
import com.didiforget.core.Result

/**
 * Contrato para cualquier proveedor de sugerencias (patrón **Strategy**).
 *
 * Ninguna otra capa conoce si detrás hay un modelo local, OpenAI, Gemini o
 * Claude: solo conocen esta interfaz. Cambiar de proveedor es cambiar UNA
 * línea en [com.didiforget.di.AppContainer], nunca tocar ViewModels ni UI.
 */
interface AIService {
    suspend fun suggestItems(description: String): Result<AISuggestion>
}

/** Respuesta estructurada, análoga al JSON descrito en el README del proyecto. */
data class AISuggestion(
    val activityName: String,
    val itemNames: List<String>
)

fun emptyDescriptionFailure(): Result<AISuggestion> = Result.Failure(DomainError.EmptyDescription)
