package com.didiforget.ui.icons

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.didiforget.R
import com.didiforget.ui.theme.AccentAmber
import com.didiforget.ui.theme.AccentBlue
import com.didiforget.ui.theme.AccentLilac
import com.didiforget.ui.theme.AccentTeal
import java.text.Normalizer

data class ItemVisual(@DrawableRes val icon: Int, val tint: Color)

private fun String.slug(): String =
    Normalizer.normalize(trim().lowercase(), Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")

private val itemVisuals: Map<String, ItemVisual> = mapOf(
    "computador" to ItemVisual(R.drawable.ic_device_laptop, AccentBlue),
    "cargador" to ItemVisual(R.drawable.ic_bolt, AccentAmber),
    "cuaderno" to ItemVisual(R.drawable.ic_notebook, AccentAmber),
    "lapiz" to ItemVisual(R.drawable.ic_pencil, AccentAmber),
    "documento de identificacion" to ItemVisual(R.drawable.ic_id, AccentBlue),
    "documento" to ItemVisual(R.drawable.ic_file_text, AccentBlue),
    "proyecto" to ItemVisual(R.drawable.ic_presentation, AccentBlue),
    "audifonos" to ItemVisual(R.drawable.ic_headphones, AccentLilac),
    "ropa" to ItemVisual(R.drawable.ic_shirt, AccentLilac),
    "ropa deportiva" to ItemVisual(R.drawable.ic_jacket, AccentLilac),
    "dinero" to ItemVisual(R.drawable.ic_wallet, AccentAmber),
    "equipaje" to ItemVisual(R.drawable.ic_luggage, AccentLilac),
    "carpa" to ItemVisual(R.drawable.ic_tent, AccentTeal),
    "linterna" to ItemVisual(R.drawable.ic_bulb, AccentAmber),
    "agua" to ItemVisual(R.drawable.ic_droplet, AccentTeal),
    "botella de agua" to ItemVisual(R.drawable.ic_bottle, AccentTeal),
    "comida" to ItemVisual(R.drawable.ic_bowl_spoon, AccentAmber),
    "botiquin" to ItemVisual(R.drawable.ic_first_aid_kit, AccentTeal),
    "toalla" to ItemVisual(R.drawable.ic_wash_dry, AccentLilac),
    "carne de salud" to ItemVisual(R.drawable.ic_heartbeat, AccentTeal),
    "resultados de examenes" to ItemVisual(R.drawable.ic_report_medical, AccentTeal)
)

private val fallbackItem = ItemVisual(R.drawable.ic_package, AccentTeal)

fun visualForItem(name: String): ItemVisual =
    itemVisuals[name.slug()] ?: fallbackItem

private val activityVisuals: List<Pair<String, ItemVisual>> = listOf(
    "universidad" to ItemVisual(R.drawable.ic_school, AccentAmber),
    "presentacion" to ItemVisual(R.drawable.ic_presentation, AccentBlue),
    "clase" to ItemVisual(R.drawable.ic_books, AccentAmber),
    "casa" to ItemVisual(R.drawable.ic_home, AccentTeal),
    "trabajo" to ItemVisual(R.drawable.ic_briefcase, AccentBlue),
    "viaje" to ItemVisual(R.drawable.ic_compass, AccentLilac),
    "vuelo" to ItemVisual(R.drawable.ic_plane, AccentLilac),
    "acampar" to ItemVisual(R.drawable.ic_tent, AccentAmber),
    "gimnasio" to ItemVisual(R.drawable.ic_barbell, AccentLilac),
    "gym" to ItemVisual(R.drawable.ic_barbell, AccentLilac),
    "cita medica" to ItemVisual(R.drawable.ic_stethoscope, AccentTeal)
)

private val fallbackActivity = ItemVisual(R.drawable.ic_list_check, AccentTeal)

/** Mismo criterio de match que LocalKeywordAIService: `contains` sobre el slug. */
fun visualForActivity(name: String): ItemVisual {
    val slug = name.slug()
    return activityVisuals.firstOrNull { slug.contains(it.first) }?.second ?: fallbackActivity
}
