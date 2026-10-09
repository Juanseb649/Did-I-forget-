# Backend — proxy de IA

Función serverless (Node/TypeScript, desplegada en [Vercel](https://vercel.com))
con un solo propósito: recibir la descripción de una actividad desde la app
de Wear OS, llamar a la API de OpenAI, y devolver el nombre de la actividad +
la lista de objetos sugeridos.

## Por qué existe

La app corre sola en el reloj (`com.google.android.wearable.standalone`) y
podría llamar a OpenAI directamente. No lo hace porque este proyecto es
público (el repo lo revisan reclutadores) y el APK se comparte: una API key
embebida en la app — aunque esté en `BuildConfig` y nunca en el código fuente
del repo — queda dentro del `.apk` compilado, y cualquiera que lo
descompile puede extraerla y gastar el cupo de OpenAI de quien la generó.

Este backend es el único lugar que conoce esa key. La app solo conoce la URL
de este proxy y un secreto compartido (`APP_SHARED_SECRET`) — ver
`app/src/main/java/com/didiforget/ai/RemoteAIService.kt`.

**Limitación conocida:** el secreto compartido evita el abuso casual/scanners
automatizados, pero no es un secreto perfecto (vive en el APK igual que
viviría la key). No hay rate-limiting persistente todavía — quedaría como
siguiente paso natural (ej. Upstash Redis) si el proyecto crece más allá de
un portafolio.

## Desarrollo local

```bash
cd server
npm install
cp .env.example .env     # y completa OPENAI_API_KEY y APP_SHARED_SECRET
npx vercel dev            # levanta el endpoint en http://localhost:3000
```

Prueba rápida:

```bash
curl -X POST http://localhost:3000/api/suggest \
  -H "Content-Type: application/json" \
  -H "x-app-secret: <el mismo valor de APP_SHARED_SECRET>" \
  -d '{"description": "voy a acampar este fin de semana"}'
```

## Despliegue (Vercel, capa gratuita)

1. `npm install -g vercel` (una sola vez).
2. `cd server && vercel login`.
3. `vercel link` — crea/asocia el proyecto en tu cuenta de Vercel.
4. Configura las variables de entorno de producción:
   ```bash
   vercel env add OPENAI_API_KEY production
   vercel env add APP_SHARED_SECRET production
   ```
5. `vercel --prod` — despliega y te da la URL pública (algo como
   `https://did-i-forget-ai-proxy.vercel.app`).
6. En el proyecto Android, agrega a `local.properties` (nunca se sube a git):
   ```properties
   BACKEND_BASE_URL=https://tu-proyecto.vercel.app
   APP_SHARED_SECRET=el-mismo-valor-del-paso-4
   ```

## Endpoint

`POST /api/suggest`

```json
// Request
{ "description": "voy a acampar este fin de semana" }

// Response 200
{ "activityName": "Acampar", "itemNames": ["Carpa", "Linterna", "Agua", "Ropa"] }
```

Respuestas de error: `400` (descripción vacía o demasiado larga), `401`
(falta o no coincide `x-app-secret`), `502` (OpenAI falló o devolvió algo
inválido), `500` (falta `OPENAI_API_KEY` en el entorno).
