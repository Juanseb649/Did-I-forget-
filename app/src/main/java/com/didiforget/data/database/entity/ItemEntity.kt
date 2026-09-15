package com.didiforget.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.didiforget.data.model.Item
import com.didiforget.data.model.ItemSource

@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(
            entity = ActivityEntity::class,
            parentColumns = ["id"],
            childColumns = ["activityId"],
            onDelete = ForeignKey.CASCADE // si se borra la actividad, se borran sus ítems
        )
    ],
    indices = [Index("activityId")]
)
data class ItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val activityId: Long,
    val name: String,
    val isChecked: Boolean,
    val source: String // se guarda el `name` del enum; ver Converters.kt
)

fun ItemEntity.toDomain(): Item = Item(
    id = id,
    activityId = activityId,
    name = name,
    isChecked = isChecked,
    source = runCatching { ItemSource.valueOf(source) }.getOrDefault(ItemSource.MANUAL)
)

fun Item.toEntity(): ItemEntity = ItemEntity(
    id = id,
    activityId = activityId,
    name = name,
    isChecked = isChecked,
    source = source.name
)
