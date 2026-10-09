package com.didiforget.domain.usecase

import com.didiforget.ai.AIService
import com.didiforget.ai.AISuggestion
import com.didiforget.ai.emptyDescriptionFailure
import com.didiforget.core.Result
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Este test es posible SIN Android, SIN Room y SIN un smartwatch real,
 * precisamente porque [GenerateChecklistUseCase] solo depende de la
 * interfaz [AIService] (Dependency Inversion). Aquí se usa una implementación
 * falsa ("test double") en vez de [com.didiforget.ai.LocalKeywordAIService].
 */
class GenerateChecklistUseCaseTest {

    private class FakeAIService(private val result: Result<AISuggestion>) : AIService {
        override suspend fun suggestItems(description: String): Result<AISuggestion> = result
    }

    @Test
    fun `cuando la IA sugiere objetos, la actividad generada los incluye`() = runBlocking {
        val fakeService = FakeAIService(
            Result.Success(AISuggestion(activityName = "Acampar", itemNames = listOf("Carpa", "Linterna")))
        )
        val useCase = GenerateChecklistUseCase(fakeService)

        val result = useCase("Voy a acampar este fin de semana")

        assertTrue(result is Result.Success)
        val activity = (result as Result.Success).data
        assertEquals("Acampar", activity.name)
        assertEquals(listOf("Carpa", "Linterna"), activity.items.map { it.name })
    }

    @Test
    fun `cuando la descripcion esta vacia, no se llama a la IA y se devuelve error`() = runBlocking {
        val fakeService = FakeAIService(emptyDescriptionFailure())
        val useCase = GenerateChecklistUseCase(fakeService)

        val result = useCase("")

        assertTrue(result is Result.Failure)
    }
}
