package com.didiforget.data.repository

import com.didiforget.data.model.Item
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun observeItems(activityId: Long): Flow<List<Item>>
    suspend fun getItems(activityId: Long): List<Item>
    suspend fun addItems(activityId: Long, items: List<Item>): List<Item>
    suspend fun setChecked(itemId: Long, checked: Boolean)
    suspend fun deleteItem(item: Item)
}
