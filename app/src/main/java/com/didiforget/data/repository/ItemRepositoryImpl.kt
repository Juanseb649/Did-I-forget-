package com.didiforget.data.repository

import com.didiforget.data.dao.ItemDao
import com.didiforget.data.database.entity.toDomain
import com.didiforget.data.database.entity.toEntity
import com.didiforget.data.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Comparte la [RecentActivitiesCache] con `ActivityRepositoryImpl`: cualquier
 * cambio en un item deja obsoleta la actividad cacheada, así que se limpia
 * (máx. 5 entradas, coste trivial).
 */
class ItemRepositoryImpl(
    private val itemDao: ItemDao,
    private val recentCache: RecentActivitiesCache = RecentActivitiesCache()
) : ItemRepository {

    override fun observeItems(activityId: Long): Flow<List<Item>> =
        itemDao.observeByActivity(activityId).map { list -> list.map { it.toDomain() } }

    override suspend fun getItems(activityId: Long): List<Item> =
        itemDao.getByActivity(activityId).map { it.toDomain() }

    override suspend fun addItems(activityId: Long, items: List<Item>): List<Item> {
        val entities = items.map { it.copy(activityId = activityId).toEntity() }
        val ids = itemDao.insertAll(entities)
        return items.mapIndexed { index, item ->
            item.copy(id = ids.getOrElse(index) { item.id }, activityId = activityId)
        }
    }

    override suspend fun setChecked(itemId: Long, checked: Boolean) {
        itemDao.setChecked(itemId, checked)
        recentCache.clear()
    }

    override suspend fun deleteItem(item: Item) {
        itemDao.delete(item.toEntity())
        recentCache.clear()
    }
}
