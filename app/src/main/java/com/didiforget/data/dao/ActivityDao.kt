package com.didiforget.data.dao

import com.didiforget.data.model.Activity

interface ActivityDao {
    fun getAll(): List<Activity>
    fun getById(id: Long): Activity?
    fun save(activity: Activity): Activity
    fun delete(id: Long)
}
