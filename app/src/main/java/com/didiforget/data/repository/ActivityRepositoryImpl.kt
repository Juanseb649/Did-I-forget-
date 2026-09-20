package com.didiforget.data.repository

import com.didiforget.data.dao.ActivityDao
import com.didiforget.data.dao.ItemDao
import com.didiforget.data.database.entity.toDomain
import com.didiforget.data.database.entity.toEntity
import com.didiforget.data.model.Activity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Implementación concreta respaldada por Room. `ActivityViewModel` y los
 * `UseCase` nunca ven esta clase directamente: reciben una
 * [ActivityRepository] (la interfaz) inyectada desde [com.didiforget.di.AppContainer].
 */
class ActivityRepositoryImpl(
    private val activityDao: ActivityDao,
    private val itemDao: ItemDao,
    private val recentCache: RecentActivitiesCache = RecentActivitiesCache()
) : ActivityRepository {

    // Observa ambas tablas: así Home se refresca también cuando cambian los
    // items (marcar/eliminar), no solo cuando cambian las actividades. Agrupa
    // los items en memoria por activityId (una sola consulta, sin N+1).
    override fun observeActivities(): Flow<List<Activity>> =
        combine(activityDao.observeAll(), itemDao.observeAll()) { activities, items ->
            val itemsByActivity = items.groupBy { it.activityId }
            activities.map { entity ->
                entity.toDomain(itemsByActivity[entity.id].orEmpty().map { it.toDomain() })
            }
        }

    override suspend fun getActivities(): List<Activity> {
        val activityEntities = activityDao.getAll()
        val ids = activityEntities.map { it.id }
        val itemsByActivity = itemDao.getByActivities(ids).groupBy { it.activityId }
        return activityEntities.map { entity ->
            entity.toDomain(itemsByActivity[entity.id].orEmpty().map { it.toDomain() })
        }
    }

    override suspend fun getActivity(id: Long): Activity? {
        recentCache.get(id)?.let { return it }

        val entity = activityDao.getById(id) ?: return null
        val items = itemDao.getByActivity(id).map { it.toDomain() }
        val activity = entity.toDomain(items)
        recentCache.put(activity)
        return activity
    }

    override suspend fun saveActivity(activity: Activity): Activity {
        val savedId = if (activity.id == 0L) {
            activityDao.insert(activity.toEntity())
        } else {
            activityDao.update(activity.toEntity())
            activity.id
        }

        val savedItems = itemDao.insertAll(
            activity.items.map { it.copy(activityId = savedId).toEntity() }
        )

        val savedActivity = activity.copy(
            id = savedId,
            items = activity.items.mapIndexed { index, item ->
                item.copy(id = savedItems.getOrElse(index) { item.id }, activityId = savedId)
            }
        )
        recentCache.put(savedActivity)
        return savedActivity
    }

    override suspend fun deleteActivity(id: Long) {
        activityDao.deleteById(id)
        recentCache.invalidate(id)
    }
}
