# Cómo abrir y ejecutar el proyecto

## 1. Requisitos

* Android Studio (versión reciente, con soporte para AGP 8.6 / Kotlin 2.0).
* JDK 17 (Android Studio suele traer uno embebido en Settings → Build Tools → Gradle).
* Un emulador de Wear OS (Device Manager → Create Device → categoría "Wear OS", API 30+) o un reloj físico con Wear OS 3+ y depuración USB/ADB activada.

## 2. Abrir el proyecto

1. Abre Android Studio → **Open** → selecciona la carpeta `Did-I-forget-`.
2. Si Android Studio pregunta por el Gradle Wrapper (no se incluyó `gradlew`/`gradlew.bat` binarios en esta entrega), déjalo generarlo automáticamente, o corre en una terminal dentro del proyecto:
   ```bash
   gradle wrapper --gradle-version 8.9
   ```
   (ya tienes Gradle instalado localmente, así que este comando debería funcionar tal cual).
3. Espera a que termine el **Gradle Sync**. Si alguna versión de librería no está disponible (pueden salir versiones nuevas después de esta entrega), Android Studio te va a sugerir el reemplazo correcto — acéptalo, no debería requerir cambios de arquitectura.

## 3. Ejecutar

1. Crea/inicia un emulador de Wear OS desde el Device Manager.
2. Selecciona el emulador como dispositivo de destino y presiona **Run ▶**.
3. Deberías ver la pantalla `HomeScreen` vacía con el botón "+ Nueva actividad".

## 4. Qué ya funciona (Fase 1 y 2 del roadmap del README)

* Crear una actividad manualmente o generar su checklist con una IA local (`LocalKeywordAIService`, basada en palabras clave — no necesita internet ni API key todavía).
* Marcar objetos en la checklist.
* Verificar y ver el resultado ("TODO LISTO" / "TE FALTA…"), con vibración cuando falta algo.
* Persistencia local con Room (las actividades sobreviven a cerrar la app).

## 5. Qué falta (a propósito, ver ARCHITECTURE.md sección 6-7)

* Conectar un proveedor de IA real (OpenAI/Gemini/Claude) — implementar `AIService` y registrar la clase en `AppContainer.kt`. El resto del código no necesita cambios.
* Refinar la entrada de texto (`SimpleTextField` en `ActivityScreen.kt`) con el flujo de voz de Wear OS (`RemoteInput`), que es la forma más natural de escribir en un reloj.
* Pulir iconografía real (los `ic_launcher_*.xml` actuales son un placeholder vectorial simple).

## 6. Correr las pruebas unitarias

```bash
./gradlew test
```

Los tests en `app/src/test/java/com/didiforget/...` no necesitan un emulador: prueban `GenerateChecklistUseCase` y `RecentActivitiesCache` de forma aislada, gracias a que dependen de interfaces (ver ARCHITECTURE.md, "Dependency Inversion").
