package com.didiforget.data.model

import java.time.Instant

data class CheckHistory(
    val id: Long = 0,
    val activityId: Long,
    val completedItems: Int,
    val totalItems: Int,
    val checkedAt: Instant = Instant.now()
) {
    val isComplete: Boolean
        get() = completedItems == totalItems
}
