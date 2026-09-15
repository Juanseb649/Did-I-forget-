package com.didiforget.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.didiforget.data.database.entity.CheckHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: CheckHistoryEntity): Long

    @Query("SELECT * FROM check_history WHERE activityId = :activityId ORDER BY timestamp DESC")
    fun observeByActivity(activityId: Long): Flow<List<CheckHistoryEntity>>
}
