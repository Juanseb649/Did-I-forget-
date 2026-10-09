package com.didiforget.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.didiforget.data.database.Converters
import com.didiforget.data.model.CheckHistory
import com.didiforget.data.model.CheckResult

@Entity(tableName = "check_history")
@TypeConverters(Converters::class)
data class CheckHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val activityId: Long,
    val timestamp: Long,
    val result: String, // CheckResult.name
    val missingItemNames: List<String>
)

fun CheckHistoryEntity.toDomain(): CheckHistory = CheckHistory(
    id = id,
    activityId = activityId,
    timestamp = timestamp,
    result = runCatching { CheckResult.valueOf(result) }.getOrDefault(CheckResult.INCOMPLETE),
    missingItemNames = missingItemNames
)

fun CheckHistory.toEntity(): CheckHistoryEntity = CheckHistoryEntity(
    id = id,
    activityId = activityId,
    timestamp = timestamp,
    result = result.name,
    missingItemNames = missingItemNames
)
