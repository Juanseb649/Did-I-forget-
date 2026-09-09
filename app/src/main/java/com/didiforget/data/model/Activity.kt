package com.didiforget.data.model

/** An activity and the objects required to complete it. */
data class Activity(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val items: List<Item> = emptyList()
)
