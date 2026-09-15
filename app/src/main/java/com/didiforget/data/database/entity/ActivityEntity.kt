package com.didiforget.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.didiforget.data.model.Activity

/**
 * Entidad Room. Vive solo en la capa de datos: el resto de la app trabaja con
 * [com.didiforget.data.model.Activity]. Separar Entity de Domain Model evita
 * que una anotación de Room (`@Entity`, `@PrimaryKey`) se filtre a un
 * ViewModel o a un Composable.
 */
@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val description: String
)

/** Entity → Domain. Los items se pasan aparte porque vienen de otra tabla. */
fun ActivityEntity.toDomain(items: List<com.didiforget.data.model.Item> = emptyList()): Activity =
    Activity(id = id, name = name, description = description, items = items)

/** Domain → Entity. `id = 0L` le indica a Room que autogenere la clave. */
fun Activity.toEntity(): ActivityEntity =
    ActivityEntity(id = id, name = name, description = description)
