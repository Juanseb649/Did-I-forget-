package com.didiforget.data.model

/**
 * Modelo de dominio inmutable. No tiene anotaciones de Room a propósito: la
 * capa de datos (`data/database/entity`) es la única que conoce Room, y la
 * convierte hacia/desde este tipo mediante mappers. Así, cambiar el motor de
 * persistencia nunca obliga a tocar ViewModels ni UI.
 */
data class Activity(
    val id: Long = 0L,
    val name: String,
    val description: String = "",
    val items: List<Item> = emptyList()
) {
    /** Objetos totales asociados a la actividad. */
    val totalItems: Int get() = items.size

    /** Objetos que el usuario ya marcó como llevados. */
    val checkedItems: Int get() = items.count { it.isChecked }

    /** Objetos que faltan por comprobar. */
    val pendingItems: List<Item> get() = items.filterNot { it.isChecked }

    /** true si el usuario ya marcó todos los objetos de la lista. */
    val isComplete: Boolean get() = items.isNotEmpty() && pendingItems.isEmpty()
}
