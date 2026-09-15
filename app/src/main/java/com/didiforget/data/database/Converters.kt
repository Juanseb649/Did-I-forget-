package com.didiforget.data.database

import androidx.room.TypeConverter

/**
 * Room solo persiste tipos primitivos por columna. Este converter le enseña
 * a guardar una `List<String>` (nombres de objetos faltantes) como un único
 * String separado por `|`, y a reconstruirla al leer.
 */
class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String = value.joinToString(separator = "|")

    @TypeConverter
    fun toStringList(value: String): List<String> =
        if (value.isBlank()) emptyList() else value.split("|")
}
