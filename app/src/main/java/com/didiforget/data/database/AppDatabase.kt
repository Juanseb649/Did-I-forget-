package com.didiforget.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.didiforget.data.dao.ActivityDao
import com.didiforget.data.dao.CheckHistoryDao
import com.didiforget.data.dao.ItemDao
import com.didiforget.data.database.entity.ActivityEntity
import com.didiforget.data.database.entity.CheckHistoryEntity
import com.didiforget.data.database.entity.ItemEntity

@Database(
    entities = [ActivityEntity::class, ItemEntity::class, CheckHistoryEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun itemDao(): ItemDao
    abstract fun checkHistoryDao(): CheckHistoryDao

    companion object {
        private const val DATABASE_NAME = "did_i_forget.db"

        // Volatile: los cambios son visibles de inmediato a todos los hilos,
        // necesario para que el patrón singleton de doble-check sea seguro.
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DATABASE_NAME)
                .build()
    }
}
