package com.didiforget.data.model

data class Item(
    val id: Long = 0,
    val activityId: Long = 0,
    val name: String,
    val checked: Boolean = false
)
