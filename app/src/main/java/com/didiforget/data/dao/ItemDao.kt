package com.didiforget.data.dao

import com.didiforget.data.model.Item

interface ItemDao {
    fun getByActivityId(activityId: Long): List<Item>
    fun save(item: Item): Item
    fun delete(id: Long)
    fun updateChecked(id: Long, checked: Boolean)
}
