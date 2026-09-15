package com.didiforget.domain.usecase

import com.didiforget.ai.AIService
import com.didiforget.core.Result
import com.didiforget.data.model.Activity
import com.didiforget.data.model.Item
import com.didiforget.data.model.ItemSource

/**
 * Caso de uso: convertir una descripción en lenguaje natural en una
 * [Activity] con su checklist sugerida.
 *
 * Cada Use Case tiene una sola responsabilidad y una sola forma de invocarse
 * (`operator fun invoke`), lo que permite llamarlo como si fuera una función:
 * `generateChecklistUseCase(description)`.
 */
class GenerateChecklistUseCase(private val aiService: AIService) {

    suspend operator fun invoke(description: String): Result<Activity> {
        return when (val suggestion = aiService.suggestItems(description)) {
            is Result.Failure -> suggestion
            is Result.Success -> {
                val items = suggestion.data.itemNames.map { name ->
                    Item(activityId = 0L, name = name, source = ItemSource.AI_SUGGESTED)
                }
                Result.Success(
                    Activity(
                        name = suggestion.data.activityName,
                        description = description,
                        items = items
                    )
                )
            }
        }
    }
}
