package com.didiforget.viewmodel

import com.didiforget.data.model.Activity
import com.didiforget.data.repository.ActivityRepository

class HomeViewModel(private val activityRepository: ActivityRepository) {
    fun loadActivities(): List<Activity> = activityRepository.getActivities()
    fun deleteActivity(id: Long) = activityRepository.deleteActivity(id)
}
