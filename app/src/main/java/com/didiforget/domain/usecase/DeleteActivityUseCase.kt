package com.didiforget.domain.usecase

import com.didiforget.data.repository.ActivityRepository

class DeleteActivityUseCase(private val activityRepository: ActivityRepository) {
    suspend operator fun invoke(activityId: Long) = activityRepository.deleteActivity(activityId)
}
