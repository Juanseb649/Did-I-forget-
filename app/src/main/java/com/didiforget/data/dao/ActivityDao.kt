package com.didiforget.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.didiforget.data.database.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activity: ActivityEntity): Long

    @Update
    suspend fun update(activity: ActivityEntity)

    @Delete
    suspend fun delete(activity: ActivityEntity)

    @Query("DELETE FROM activities WHERE id = :activityId")
    suspend fun deleteById(activityId: Long)

    @Query("SELECT * FROM activities WHERE id = :activityId")
    suspend fun getById(activityId: Long): ActivityEntity?

    /** Flow: la UI se actualiza sola cuando la tabla cambia (sin refrescos manuales). */
    @Query("SELECT * FROM activities ORDER BY id DESC")
    fun observeAll(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities ORDER BY id DESC")
    suspend fun getAll(): List<ActivityEntity>
}
