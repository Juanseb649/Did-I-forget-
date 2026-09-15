package com.didiforget.data.repository

import com.didiforget.data.model.Activity

/**
 * Caché LRU (Least Recently Used) en memoria para las actividades consultadas
 * recientemente.
 *
 * Estructura de datos: [LinkedHashMap] construido con `accessOrder = true`,
 * de forma que cada `get` reordena la entrada al final (la marca como "más
 * reciente"). Al superar [maxSize], se expulsa automáticamente la entrada
 * menos usada mediante `removeEldestEntry`.
 *
 * Complejidad: O(1) amortizado para `get` y `put`, igual que un `HashMap`
 * normal, pero con orden de recencia — ideal para un reloj donde evitar una
 * consulta a Room ahorra tiempo y batería.
 */
class RecentActivitiesCache(private val maxSize: Int = 5) {

    private val cache = object : LinkedHashMap<Long, Activity>(
        /* initialCapacity = */ maxSize,
        /* loadFactor = */ 0.75f,
        /* accessOrder = */ true
    ) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Long, Activity>?): Boolean =
            size > maxSize
    }

    @Synchronized
    fun get(id: Long): Activity? = cache[id]

    @Synchronized
    fun put(activity: Activity) {
        cache[activity.id] = activity
    }

    @Synchronized
    fun invalidate(id: Long) {
        cache.remove(id)
    }

    @Synchronized
    fun clear() = cache.clear()
}
