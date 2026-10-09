package com.didiforget.data.repository

import com.didiforget.data.model.CheckHistory
import kotlinx.coroutines.flow.Flow

interface CheckHistoryRepository {
    fun observeHistory(activityId: Long): Flow<List<CheckHistory>>
    suspend fun record(entry: CheckHistory)
}
