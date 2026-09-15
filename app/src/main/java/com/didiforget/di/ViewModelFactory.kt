package com.didiforget.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.didiforget.viewmodel.ActivityViewModel
import com.didiforget.viewmodel.ChecklistViewModel
import com.didiforget.viewmodel.HomeViewModel

/**
 * Fábrica genérica que le enseña al sistema de ViewModels de Android cómo
 * construir nuestros ViewModels (que reciben dependencias por constructor en
 * vez de tener un constructor vacío). Sin esto, `ViewModelProvider` no sabría
 * de dónde sacar un [com.didiforget.data.repository.ActivityRepository].
 */
class ViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        HomeViewModel::class.java -> HomeViewModel(
            container.activityRepository,
            container.deleteActivityUseCase
        )

        ActivityViewModel::class.java -> ActivityViewModel(
            container.generateChecklistUseCase,
            container.saveActivityUseCase
        )

        ChecklistViewModel::class.java -> ChecklistViewModel(
            container.activityRepository,
            container.toggleItemUseCase,
            container.verifyChecklistUseCase
        )

        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    } as T
}
