package com.didiforget.data.repository

import com.didiforget.data.dao.ItemDao
import com.didiforget.data.model.Item

class ItemRepository(private val itemDao: ItemDao) {
    fun getItems(activityId: Long): List<Item> = itemDao.getByActivityId(activityId)
    fun saveItem(item: Item): Item = itemDao.save(item)
    fun deleteItem(id: Long) = itemDao.delete(id)
    fun setChecked(id: Long, checked: Boolean) = itemDao.updateChecked(id, checked)
}
