package com.didiforget.data.database

import com.didiforget.data.dao.ActivityDao
import com.didiforget.data.dao.ItemDao

/** Database boundary. Replace the DAO providers with Room implementations. */
interface AppDatabase {
    val activityDao: ActivityDao
    val itemDao: ItemDao
}
