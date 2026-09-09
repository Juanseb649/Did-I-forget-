package com.didiforget.viewmodel

import com.didiforget.ai.AIService
import com.didiforget.data.model.Activity
import com.didiforget.data.model.Item
import com.didiforget.data.repository.ActivityRepository
import com.didiforget.data.repository.ItemRepository

class ActivityViewModel(
    private val activityRepository: ActivityRepository,
    private val itemRepository: ItemRepository,
    private val aiService: AIService
) {
    fun saveActivity(activity: Activity): Activity = activityRepository.saveActivity(activity)
    fun loadItems(activityId: Long): List<Item> = itemRepository.getItems(activityId)
    fun setItemChecked(id: Long, checked: Boolean) = itemRepository.setChecked(id, checked)
    suspend fun suggestItems(description: String): List<Item> = aiService.suggestItems(description)
}
