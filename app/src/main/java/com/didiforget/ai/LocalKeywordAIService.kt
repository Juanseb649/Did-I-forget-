package com.didiforget.ai

import com.didiforget.core.Result

/**
 * Implementación por defecto de [AIService] mientras no se elige un proveedor
 * externo (OpenAI, Gemini, Claude, etc.).
 *
 * No hace ninguna llamada de red: interpreta la descripción con coincidencia
 * de palabras clave sobre un diccionario en memoria. Es una app real y
 * funcional desde el día uno, y el día que se conecte un proveedor real solo
 * hay que escribir otra clase que implemente [AIService] — el resto de la
 * app no se entera del cambio (ver ARCHITECTURE.md, patrón Strategy).
 *
 * Estructura de datos: un `Map<String, List<String>>` funciona aquí como un
 * índice invertido simplificado — cada palabra clave apunta a la lista de
 * objetos sugeridos para esa categoría de actividad.
 */
class LocalKeywordAIService : AIService {

    private val keywordCatalog: Map<String, List<String>> = mapOf(
        "universidad" to listOf("Computador", "Cargador", "Cuaderno", "Documento de identificación"),
        "presentacion" to listOf("Computador", "Cargador", "Proyecto", "Documento de identificación"),
        "clase" to listOf("Cuaderno", "Lápiz", "Computador", "Audífonos"),
        "trabajo" to listOf("Computador", "Cargador", "Documento de identificación", "Audífonos"),
        "viaje" to listOf("Documento", "Ropa", "Cargador", "Audífonos", "Dinero", "Equipaje"),
        "acampar" to listOf("Carpa", "Linterna", "Agua", "Comida", "Ropa", "Botiquín"),
        "gimnasio" to listOf("Ropa deportiva", "Toalla", "Botella de agua", "Audífonos"),
        "cita medica" to listOf("Documento de identificación", "Carné de salud", "Resultados de exámenes")
    )

    private val defaultSuggestion = listOf("Documento de identificación", "Cargador", "Dinero")

    override suspend fun suggestItems(description: String): Result<AISuggestion> {
        val normalized = description.trim()
        if (normalized.isEmpty()) return emptyDescriptionFailure()

        val lowered = normalized.lowercase()
        val matches = keywordCatalog.entries
            .filter { (keyword, _) -> lowered.contains(keyword) }
            .flatMap { it.value }
            .distinct()

        val suggestedItems = matches.ifEmpty { defaultSuggestion }

        return Result.Success(
            AISuggestion(
                activityName = normalized.replaceFirstChar { it.uppercase() },
                itemNames = suggestedItems
            )
        )
    }
}
