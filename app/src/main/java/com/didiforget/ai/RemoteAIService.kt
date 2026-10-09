package com.didiforget.ai

import com.didiforget.BuildConfig
import com.didiforget.core.DomainError
import com.didiforget.core.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

/**
 * Implementación de [AIService] que habla con el backend propio del
 * proyecto (`server/`, una función serverless en Vercel) en vez de llamar a
 * OpenAI directamente desde el reloj.
 *
 * La API key de OpenAI vive SOLO en el servidor — ver `server/README.md`
 * para la razón: esta app es de portafolio, el repo es público y el APK se
 * comparte, así que cualquier secreto embebido en la app es extraíble del
 * binario compilado. Esta clase solo conoce la URL del proxy
 * ([BuildConfig.BACKEND_BASE_URL]) y un secreto compartido
 * ([BuildConfig.APP_SHARED_SECRET]) que ambos lados configuran por fuera del
 * repositorio (`local.properties` / variables de entorno de Vercel).
 *
 * Si [BuildConfig.BACKEND_BASE_URL] está vacío (no configurado en
 * `local.properties`), [com.didiforget.di.AppContainer] usa
 * [LocalKeywordAIService] en su lugar — ver ahí el porqué.
 */
class RemoteAIService(
    private val baseUrl: String = BuildConfig.BACKEND_BASE_URL,
    private val sharedSecret: String = BuildConfig.APP_SHARED_SECRET
) : AIService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    override suspend fun suggestItems(description: String): Result<AISuggestion> {
        val normalized = description.trim()
        if (normalized.isEmpty()) return emptyDescriptionFailure()

        return withContext(Dispatchers.IO) {
            try {
                val body = JSONObject().put("description", normalized).toString()
                    .toRequestBody(JSON_MEDIA_TYPE)

                val request = Request.Builder()
                    .url("${baseUrl.trimEnd('/')}/api/suggest")
                    .header("x-app-secret", sharedSecret)
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    val rawBody = response.body?.string().orEmpty()
                    if (!response.isSuccessful) {
                        return@withContext Result.Failure(DomainError.AiUnavailable)
                    }

                    val json = JSONObject(rawBody)
                    val activityName = json.optString("activityName").ifBlank {
                        normalized.replaceFirstChar { it.uppercase() }
                    }
                    val itemNames = json.optJSONArray("itemNames").toStringList()
                    if (itemNames.isEmpty()) {
                        return@withContext Result.Failure(DomainError.AiUnavailable)
                    }

                    Result.Success(AISuggestion(activityName = activityName, itemNames = itemNames))
                }
            } catch (ioError: IOException) {
                // Sin red, timeout, o el proxy no responde.
                Result.Failure(DomainError.AiUnavailable)
            } catch (parseError: org.json.JSONException) {
                // Respuesta inesperada del backend (no es el JSON que esperamos).
                Result.Failure(DomainError.AiUnavailable)
            }
        }
    }
}

private fun JSONArray?.toStringList(): List<String> {
    if (this == null) return emptyList()
    return (0 until length()).mapNotNull { index -> optString(index).takeIf { it.isNotBlank() } }
}
