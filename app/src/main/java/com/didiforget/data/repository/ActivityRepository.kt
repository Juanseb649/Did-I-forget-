package com.didiforget.data.repository

import com.didiforget.data.dao.ActivityDao
import com.didiforget.data.model.Activity

class ActivityRepository(private val activityDao: ActivityDao) {
    fun getActivities(): List<Activity> = activityDao.getAll()
    fun getActivity(id: Long): Activity? = activityDao.getById(id)
    fun saveActivity(activity: Activity): Activity = activityDao.save(activity)
    fun deleteActivity(id: Long) = activityDao.delete(id)
}
