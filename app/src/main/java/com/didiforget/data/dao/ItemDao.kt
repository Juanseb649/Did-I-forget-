package com.didiforget.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.didiforget.data.database.entity.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ItemEntity>): List<Long>

    @Update
    suspend fun update(item: ItemEntity)

    @Delete
    suspend fun delete(item: ItemEntity)

    @Query("UPDATE items SET isChecked = :checked WHERE id = :itemId")
    suspend fun setChecked(itemId: Long, checked: Boolean)

    @Query("SELECT * FROM items ORDER BY id ASC")
    fun observeAll(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE activityId = :activityId ORDER BY id ASC")
    fun observeByActivity(activityId: Long): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE activityId = :activityId ORDER BY id ASC")
    suspend fun getByActivity(activityId: Long): List<ItemEntity>

    @Query("SELECT * FROM items WHERE activityId IN (:activityIds)")
    suspend fun getByActivities(activityIds: List<Long>): List<ItemEntity>
}
