package com.didiforget.data.model

/**
 * Registro histórico de una verificación (cuando el usuario revisó su
 * checklist antes de salir). Permite, a futuro, mostrar estadísticas como
 * "cuántas veces se te olvidó algo antes de ir a la universidad".
 */
data class CheckHistory(
    val id: Long = 0L,
    val activityId: Long,
    val timestamp: Long,
    val result: CheckResult,
    val missingItemNames: List<String> = emptyList()
)

/** Resultado mutuamente excluyente de una verificación. Una `sealed`/`enum`
 * en vez de un `Boolean` deja explícito qué significa cada valor. */
enum class CheckResult {
    COMPLETE,
    INCOMPLETE
}
