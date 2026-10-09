package com.didiforget.data.repository

import com.didiforget.data.model.Activity
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acceso a actividades. El dominio y los ViewModels dependen SOLO
 * de esta interfaz (Dependency Inversion Principle): no saben si detrás hay
 * Room, una API o un mapa en memoria.
 */
interface ActivityRepository {
    fun observeActivities(): Flow<List<Activity>>
    suspend fun getActivities(): List<Activity>
    suspend fun getActivity(id: Long): Activity?
    suspend fun saveActivity(activity: Activity): Activity
    suspend fun deleteActivity(id: Long)
}
