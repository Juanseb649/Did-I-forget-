package com.didiforget.di

import android.content.Context
import com.didiforget.ai.AIService
import com.didiforget.ai.LocalKeywordAIService
import com.didiforget.data.database.AppDatabase
import com.didiforget.data.repository.ActivityRepository
import com.didiforget.data.repository.ActivityRepositoryImpl
import com.didiforget.data.repository.CheckHistoryRepository
import com.didiforget.data.repository.CheckHistoryRepositoryImpl
import com.didiforget.data.repository.ItemRepository
import com.didiforget.data.repository.ItemRepositoryImpl
import com.didiforget.domain.usecase.DeleteActivityUseCase
import com.didiforget.domain.usecase.GenerateChecklistUseCase
import com.didiforget.domain.usecase.SaveActivityUseCase
import com.didiforget.domain.usecase.ToggleItemUseCase
import com.didiforget.domain.usecase.VerifyChecklistUseCase

/**
 * Contenedor de dependencias manual (sin Hilt/Koin).
 *
 * Es el ÚNICO lugar de la app que sabe cómo construir cada clase concreta.
 * Todo lo demás (ViewModels, Use Cases) recibe interfaces por constructor.
 * Para cambiar de proveedor de IA, o de implementación de un repositorio,
 * se cambia UNA línea aquí — ver ARCHITECTURE.md, sección 7.
 *
 * `by lazy` asegura que cada dependencia se crea una sola vez (equivalente
 * artesanal a un scope "singleton" de un framework de DI).
 */
class AppContainer(context: Context) {

    private val database: AppDatabase by lazy { AppDatabase.getInstance(context) }

    // --- Repositorios -----------------------------------------------------
    val activityRepository: ActivityRepository by lazy {
        ActivityRepositoryImpl(database.activityDao(), database.itemDao())
    }

    val itemRepository: ItemRepository by lazy {
        ItemRepositoryImpl(database.itemDao())
    }

    val checkHistoryRepository: CheckHistoryRepository by lazy {
        CheckHistoryRepositoryImpl(database.checkHistoryDao())
    }

    // --- IA (Strategy) ------------------------------------------------------
    // Único punto de cambio cuando se elija un proveedor real (OpenAI, Gemini,
    // Claude...): reemplazar LocalKeywordAIService() por, por ejemplo,
    // OpenAIService(apiKey = BuildConfig.OPENAI_API_KEY).
    val aiService: AIService by lazy { LocalKeywordAIService() }

    // --- Use Cases ----------------------------------------------------------
    val generateChecklistUseCase: GenerateChecklistUseCase by lazy { GenerateChecklistUseCase(aiService) }
    val saveActivityUseCase: SaveActivityUseCase by lazy { SaveActivityUseCase(activityRepository) }
    val deleteActivityUseCase: DeleteActivityUseCase by lazy { DeleteActivityUseCase(activityRepository) }
    val toggleItemUseCase: ToggleItemUseCase by lazy { ToggleItemUseCase(itemRepository) }
    val verifyChecklistUseCase: VerifyChecklistUseCase by lazy { VerifyChecklistUseCase(checkHistoryRepository) }
}
