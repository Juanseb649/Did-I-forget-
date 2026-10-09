package com.didiforget.data.model

/**
 * Un objeto individual dentro de la checklist de una [Activity].
 *
 * Inmutable: marcar/desmarcar un ítem se hace con `item.copy(isChecked = ...)`,
 * nunca mutando esta instancia. Esto hace que el estado en el ViewModel sea
 * predecible y fácil de comparar (útil para que Compose sepa qué recomponer).
 */
data class Item(
    val id: Long = 0L,
    val activityId: Long,
    val name: String,
    val isChecked: Boolean = false,
    val source: ItemSource = ItemSource.MANUAL
)

/** De dónde vino este ítem: lo escribió el usuario o lo sugirió la IA. */
enum class ItemSource {
    MANUAL,
    AI_SUGGESTED
}
