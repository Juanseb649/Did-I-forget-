package com.didiforget.domain.usecase

import com.didiforget.data.model.Activity
import com.didiforget.data.model.CheckHistory
import com.didiforget.data.model.CheckResult
import com.didiforget.data.repository.CheckHistoryRepository

/**
 * Caso de uso que implementa el paso "¿Falta algo?" del flujo del README:
 * revisa la actividad, decide COMPLETE/INCOMPLETE y deja constancia en el
 * historial.
 */
class VerifyChecklistUseCase(private val checkHistoryRepository: CheckHistoryRepository) {

    suspend operator fun invoke(activity: Activity): CheckHistory {
        val missing = activity.pendingItems.map { it.name }
        val result = if (missing.isEmpty()) CheckResult.COMPLETE else CheckResult.INCOMPLETE

        val entry = CheckHistory(
            activityId = activity.id,
            timestamp = System.currentTimeMillis(),
            result = result,
            missingItemNames = missing
        )
        checkHistoryRepository.record(entry)
        return entry
    }
}
