package com.didiforget.domain.usecase

import com.didiforget.data.model.Activity
import com.didiforget.data.repository.ActivityRepository

class SaveActivityUseCase(private val activityRepository: ActivityRepository) {
    suspend operator fun invoke(activity: Activity): Activity = activityRepository.saveActivity(activity)
}
