package com.didiforget.data.repository

import com.didiforget.data.model.Activity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Verifica el comportamiento LRU descrito en ARCHITECTURE.md. */
class RecentActivitiesCacheTest {

    @Test
    fun `expulsa la entrada menos usada al superar el tamano maximo`() {
        val cache = RecentActivitiesCache(maxSize = 2)

        cache.put(Activity(id = 1, name = "Universidad"))
        cache.put(Activity(id = 2, name = "Viaje"))
        cache.get(1) // toca la actividad 1, ahora "2" es la menos reciente
        cache.put(Activity(id = 3, name = "Acampar")) // debería expulsar a "2"

        assertEquals("Universidad", cache.get(1)?.name)
        assertNull(cache.get(2))
        assertEquals("Acampar", cache.get(3)?.name)
    }
}
