package com.didiforget.data.repository

import com.didiforget.data.dao.CheckHistoryDao
import com.didiforget.data.database.entity.toDomain
import com.didiforget.data.database.entity.toEntity
import com.didiforget.data.model.CheckHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CheckHistoryRepositoryImpl(private val dao: CheckHistoryDao) : CheckHistoryRepository {

    override fun observeHistory(activityId: Long): Flow<List<CheckHistory>> =
        dao.observeByActivity(activityId).map { list -> list.map { it.toDomain() } }

    override suspend fun record(entry: CheckHistory) {
        dao.insert(entry.toEntity())
    }
}
