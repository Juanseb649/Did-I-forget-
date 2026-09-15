#  Did I Forget?

> **Asistente inteligente para smartwatch que ayuda al usuario a no olvidar objetos importantes antes de realizar una actividad.**

##  Descripción

**Did I Forget?** es una aplicación desarrollada para **smartwatch con Wear OS** cuyo objetivo es ayudar al usuario a verificar que lleva consigo los objetos necesarios antes de salir o comenzar una actividad.

La aplicación utiliza **inteligencia artificial** para generar automáticamente una lista de objetos a partir de una descripción proporcionada por el usuario.

Por ejemplo:

> "Voy a presentar mi proyecto en la universidad."

La aplicación puede sugerir:

* 💻 Computador
* 🔌 Cargador
* 📓 Cuaderno
* 📄 Proyecto
* 🪪 Documento de identificación

El usuario puede modificar la lista y posteriormente utilizar el smartwatch para comprobar cada elemento.

---

# Objetivo

Resolver un problema cotidiano:

> **Salir de casa y darse cuenta después de que olvidamos algo importante.**

Did I Forget? busca convertir el smartwatch en una herramienta rápida para realizar una última comprobación antes de salir.

La aplicación no pretende ser una aplicación de productividad tradicional ni una aplicación de fitness. Su función principal es **ayudar a verificar objetos asociados a una actividad específica**.

---

# Arquitectura del proyecto

La aplicación utiliza una arquitectura **MVVM**, una evolución de MVC adecuada para Android: la UI funciona como vista, los `ViewModel` coordinan la lógica y los repositorios/DAO gestionan el modelo y la persistencia local. La implementación base está preparada para Kotlin y Wear OS:

```text
app/src/main/java/com/didiforget/
├── ui/
│   ├── home/HomeScreen.kt
│   ├── activity/ActivityScreen.kt
│   ├── checklist/ChecklistScreen.kt
│   └── result/ResultScreen.kt
├── viewmodel/
│   ├── HomeViewModel.kt
│   └── ActivityViewModel.kt
├── data/
│   ├── model/
│   │   ├── Activity.kt
│   │   ├── Item.kt
│   │   └── CheckHistory.kt
│   ├── dao/
│   │   ├── ActivityDao.kt
│   │   └── ItemDao.kt
│   ├── database/AppDatabase.kt
│   └── repository/
│       ├── ActivityRepository.kt
│       └── ItemRepository.kt
└── ai/AIService.kt
```

## Responsabilidades

* **UI:** presenta las pantallas y comunica las acciones del usuario al `ViewModel`.
* **ViewModel:** mantiene el estado de cada flujo y coordina repositorios y sugerencias de IA.
* **Model:** define las entidades `Activity`, `Item` y `CheckHistory`.
* **DAO:** declara las operaciones de persistencia para actividades y objetos.
* **Database:** expone los DAO y será el punto de integración con Room.
* **Repository:** encapsula el acceso a datos para que la UI no dependa de la base de datos.
* **AI:** define el contrato para convertir una descripción en objetos sugeridos.

## Flujo entre capas

```text
HomeScreen
   ↓
HomeViewModel → ActivityRepository → ActivityDao → AppDatabase
   ↓
ActivityScreen → ActivityViewModel
               ├── ActivityRepository
               ├── ItemRepository
               └── AIService
   ↓
ChecklistScreen → actualización de Item
   ↓
ResultScreen → resultado de la verificación
```

Los archivos actuales contienen contratos y modelos iniciales. La siguiente integración puede conectar las pantallas con Jetpack Compose, implementar los DAO con Room y proporcionar una implementación concreta de `AIService`.

> **Importante:** `app/src/main/java` es la carpeta convencional de código fuente de Android; no significa que exista un backend Java. En este proyecto el código es Kotlin. Gradle, cuando se agregue el proyecto Android completo, será únicamente la herramienta de construcción y configuración de dependencias, no un servidor.

---

#  Flujo de la aplicación

El flujo principal del sistema será:

```text
                 DID I FORGET?
                       │
                       ↓
              ¿QUÉ VAS A HACER?
                       │
                       ↓
                   ACTIVIDAD
                       │
              ┌────────┴────────┐
              ↓                 ↓
           Manual               IA
              ↓                 ↓
              └────────┬────────┘
                       ↓
              LISTA DE OBJETOS
                       │
                       ↓
              REVISAR OBJETOS
                       │
                       ↓
                ¿FALTA ALGO?
                  ↙       ↘
                SÍ         NO
                ↓           ↓
         ⚠️ Recordar    ✅ Todo listo
```

---

#  Funcionamiento con IA

La inteligencia artificial será utilizada para interpretar el lenguaje natural del usuario y generar una lista inicial de objetos.

### Ejemplo

El usuario introduce:

> "Voy a acampar este fin de semana."

La IA genera:

```text
Actividad: Acampar

Objetos sugeridos:
- Carpa
- Linterna
- Agua
- Comida
- Ropa
- Botiquín
```

El usuario puede aceptar, modificar o eliminar elementos antes de guardar la actividad.

### Funciones de la IA

* Interpretar actividades escritas en lenguaje natural.
* Generar objetos relacionados con la actividad.
* Sugerir elementos que podrían ser necesarios.
* Convertir una descripción en una estructura de datos organizada.

La IA funcionará como un **asistente de sugerencias**, no como una fuente obligatoria de información.

---

# Flujo de usuario

## 1. Seleccionar o crear actividad

El usuario puede seleccionar una actividad existente:

```text
🎓 Universidad
💼 Trabajo
✈️ Viaje
🏕️ Acampar
```

También puede crear una nueva:

```text
+ Nueva actividad
```

---

## 2. Generar lista

El usuario puede introducir una descripción:

> "Voy a viajar durante tres días."

La IA genera una lista:

```text
✈️ Viaje

☐ Documento
☐ Ropa
☐ Cargador
☐ Audífonos
☐ Dinero
☐ Equipaje
```

---

## 3. Personalizar

El usuario puede modificar la lista:

```text
☑ Documento
☑ Ropa
☑ Cargador
☐ Audífonos
☑ Dinero
☑ Equipaje
```

También puede agregar o eliminar objetos manualmente.

---

## 4. Verificación

Antes de salir, el usuario abre la actividad desde el smartwatch.

```text
✈️ VIAJE

☑ Documento
☑ Ropa
☑ Cargador
☐ Audífonos
☑ Dinero
☑ Equipaje
```

El usuario marca cada elemento conforme comprueba que lo lleva.

---

## 5. Resultado

Si falta algún objeto:

```text
 TE FALTA

 Audífonos
```

Si todos están completos:

```text
 TODO LISTO

Puedes salir.
```

El smartwatch puede utilizar **vibración/haptic feedback** para llamar la atención del usuario cuando sea necesario.

---

#  Persistencia local

Did I Forget? **no utilizará un backend ni una base de datos remota**.

La información será almacenada localmente en el dispositivo mediante **Room**.

Esto permitirá guardar:

* Actividades.
* Objetos.
* Listas personalizadas.
* Historial de verificaciones.
* Estado de los objetos.

### Estructura conceptual

```text
Activity
│
├── name
├── description
└── items
      │
      ├── name
      └── completed
```

Ejemplo:

```text
Universidad
│
├── Computador       ✓
├── Cargador         ✓
├── Cuaderno         ✗
└── Audífonos        ✓
```

El uso de almacenamiento local permite mantener el proyecto sencillo y evita crear infraestructura innecesaria.

---

# Arquitectura

La aplicación tendrá una arquitectura principalmente local:

```text
┌─────────────────────────────┐
│         Wear OS App         │
│                             │
│     Kotlin + Compose        │
│                             │
│  ┌───────────────────────┐  │
│  │    Interfaz / UI      │  │
│  └───────────┬───────────┘  │
│              ↓              │
│  ┌───────────────────────┐  │
│  │      App Logic        │  │
│  └───────────┬───────────┘  │
│              ↓              │
│  ┌───────────────────────┐  │
│  │       Room DB         │  │
│  │      Base local       │  │
│  └───────────────────────┘  │
└──────────────┬──────────────┘
               │
               │ Consulta IA
               ↓
       ┌─────────────────┐
       │   AI Service    │
       │   API externa   │
       └─────────────────┘
```

La única comunicación externa será la necesaria para utilizar el servicio de inteligencia artificial.

La base de datos Room se ejecutará localmente en el smartwatch. No se creará backend propio ni servidor para las actividades, los objetos o el historial.

---

#  Tecnologías

## Lenguaje

### Kotlin

Lenguaje principal utilizado para desarrollar la aplicación Android y Wear OS.

---

## Interfaz

### Jetpack Compose for Wear OS

Framework moderno de Android utilizado para construir la interfaz del smartwatch mediante Kotlin.

Se utilizará para desarrollar:

* Pantalla principal.
* Listas.
* Botones.
* Checkboxes.
* Navegación.
* Animaciones.
* Estados visuales.
* Componentes adaptados a pantallas pequeñas.

---

## Base de datos local

### Room

Biblioteca de persistencia de Android utilizada para almacenar información localmente.

Permitirá guardar las actividades y listas sin necesidad de un servidor.

---

## Plataforma

### Wear OS

Sistema operativo utilizado para ejecutar la aplicación en dispositivos smartwatch compatibles.

---

## Desarrollo

### Android Studio

Entorno de desarrollo utilizado para:

* Programar en Kotlin.
* Diseñar la interfaz con Compose.
* Ejecutar el emulador de Wear OS.
* Depurar la aplicación.
* Generar la aplicación.

---

#  Integración de IA

La aplicación utilizará una API de inteligencia artificial capaz de procesar lenguaje natural.

El flujo será:

```text
Usuario
   ↓
"Voy a acampar"
   ↓
Aplicación
   ↓
API de IA
   ↓
Respuesta estructurada
   ↓
Lista de objetos
   ↓
Room
```

La respuesta ideal de la IA tendrá una estructura similar a:

```json
{
  "activity": "Acampar",
  "items": [
    "Carpa",
    "Linterna",
    "Agua",
    "Comida",
    "Ropa",
    "Botiquín"
  ]
}
```

La aplicación utilizará esta información para construir automáticamente la checklist.

---

# Componentes principales

La aplicación se puede dividir inicialmente en los siguientes componentes:

### HomeScreen

Pantalla principal de la aplicación.

Permite acceder a las actividades y crear nuevas listas.

### ActivityScreen

Muestra una actividad y sus objetos asociados.

### ChecklistItem

Componente reutilizable para representar cada objeto.

Ejemplo:

```text
☑ Computador
```

### ActivityCard

Representa una actividad guardada.

Ejemplo:

```text
 Universidad
6 objetos
```

### ResultScreen

Muestra el resultado de la verificación:

* Todo listo.
* Objetos pendientes.

### AIService

Se encarga de comunicarse con la API de inteligencia artificial.

### ActivityRepository

Gestiona las actividades almacenadas localmente.

### ItemRepository

Gestiona los objetos asociados a cada actividad.

---

#  Estructura inicial del proyecto

Una posible estructura sería:

```text
app/
│
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── ActivityDao.kt
│   │   └── ItemDao.kt
│   │
│   ├── model/
│   │   ├── Activity.kt
│   │   └── Item.kt
│   │
│   └── repository/
│       ├── ActivityRepository.kt
│       └── ItemRepository.kt
│
├── ai/
│   └── AIService.kt
│
├── ui/
│   ├── screens/
│   │   ├── HomeScreen.kt
│   │   ├── ActivityScreen.kt
│   │   └── ResultScreen.kt
│   │
│   └── components/
│       ├── ActivityCard.kt
│       ├── ChecklistItem.kt
│       └── PrimaryButton.kt
│
└── MainActivity.kt
```

La estructura puede modificarse a medida que avance el desarrollo.

---

#  Diseño de interfaz

Debido a que la aplicación está diseñada para smartwatch, la interfaz debe priorizar:

* Información clara.
* Pocos elementos por pantalla.
* Botones grandes.
* Texto fácil de leer.
* Navegación sencilla.
* Interacciones rápidas.
* Uso de vibración para alertas.
* Estados visuales claros.

El objetivo es que el usuario pueda comprobar una lista en **pocos segundos**.

---

#  Decisiones de arquitectura

Para mantener el proyecto enfocado y evitar complejidad innecesaria:

### No se utilizará:

* Backend propio.
* Spring Boot.
* PostgreSQL.
* Servidor dedicado.
* Autenticación mediante cuentas.
* Sincronización en la nube.

### Sí se utilizará:

* Kotlin.
* Wear OS.
* Jetpack Compose.
* Room.
* Almacenamiento local.
* API de inteligencia artificial.

Estas decisiones permiten concentrar el desarrollo en la experiencia del smartwatch, la persistencia local y la integración de IA.

---

#  Fases de desarrollo

## Fase 1 — Interfaz

Crear:

* Pantalla principal.
* Actividades.
* Checklist.
* Resultado.

```text
Smartwatch
    ↓
UI
    ↓
Checklist
```

---

## Fase 2 — Persistencia

Implementar Room para guardar:

* Actividades.
* Objetos.
* Estados.
* Historial.

```text
UI
 ↓
Room
 ↓
Datos locales
```

---

## Fase 3 — Inteligencia artificial

Agregar la comunicación con la API de IA.

```text
Actividad
    ↓
IA
    ↓
Lista sugerida
    ↓
Room
```

---

## Fase 4 — Funciones del smartwatch

Agregar:

* Vibración.
* Notificaciones.
* Navegación optimizada.
* Animaciones.
* Feedback visual.
* Adaptación a diferentes tamaños de pantalla.

---

#  MVP

La primera versión funcional de Did I Forget? deberá permitir:

* [ ] Crear una actividad.
* [ ] Agregar objetos.
* [ ] Mostrar una checklist.
* [ ] Marcar objetos como completados.
* [ ] Mostrar objetos pendientes.
* [ ] Mostrar mensaje de "Todo listo".
* [ ] Guardar actividades localmente.
* [ ] Generar una lista mediante IA.
* [ ] Modificar la lista generada por IA.
* [ ] Utilizar vibración para alertas.

---

#  Ejemplo de uso

### Usuario

> "Voy a la universidad a presentar un proyecto."

### IA

```text
 Presentación universitaria

☐ Computador
☐ Cargador
☐ Proyecto
☐ Cuaderno
☐ Documento
```

### Usuario

Modifica la lista y guarda la actividad.

### Antes de salir

El smartwatch muestra:

```text
 UNIVERSIDAD

☑ Computador
☑ Cargador
☑ Proyecto
☐ Cuaderno
☑ Documento
```

El reloj vibra:

>  Te falta: Cuaderno

El usuario marca el objeto.

Finalmente:

```text
 TODO LISTO

¡Puedes salir!
```

---

#  Resumen

**Did I Forget?** será una aplicación de smartwatch enfocada en resolver una tarea específica y cotidiana: **comprobar que el usuario no olvide objetos importantes antes de realizar una actividad**.

El proyecto utilizará:

```text
Kotlin
   +
Jetpack Compose for Wear OS
   +
Room
   +
IA
```

La aplicación no contará con backend propio. Los datos personales y las actividades se almacenarán localmente, mientras que la inteligencia artificial se utilizará únicamente para generar sugerencias inteligentes de objetos.

Esta arquitectura permite mantener el proyecto pequeño, dedicar más tiempo al diseño de la interfaz y aprovechar las características propias de un smartwatch.

# Arquitectura — Did I Forget?

Este documento explica **cómo** está construida la aplicación y **por qué**, para que cualquier persona (incluido tu yo del futuro) pueda entender las decisiones de diseño sin tener que releer todo el código.

## 1. Estilo arquitectónico: MVVM por capas

La app usa **MVVM** (Model-View-ViewModel) combinado con una separación en capas inspirada en Clean Architecture, pero simplificada para el tamaño del proyecto:

```text
┌─────────────────────────────────────────────────────────┐
│                         UI (View)                        │
│   Composables de Wear OS: HomeScreen, ActivityScreen,     │
│   ChecklistScreen, ResultScreen                            │
└───────────────────────────┬─────────────────────────────┘
                             │ observa StateFlow<UiState<T>>
┌───────────────────────────▼─────────────────────────────┐
│                        ViewModel                          │
│   HomeViewModel, ActivityViewModel, ChecklistViewModel     │
│   - Exponen estado inmutable (StateFlow)                   │
│   - Traducen eventos de UI en llamadas a Use Cases          │
└───────────────────────────┬─────────────────────────────┘
                             │ invoca
┌───────────────────────────▼─────────────────────────────┐
│                     Domain (Use Cases)                     │
│   GenerateChecklistUseCase, SaveActivityUseCase,            │
│   ToggleItemUseCase, VerifyChecklistUseCase,                │
│   DeleteActivityUseCase                                     │
│   - Una responsabilidad de negocio por clase (SRP)          │
└───────────────────────────┬─────────────────────────────┘
                             │ depende de interfaces
┌───────────────────────────▼─────────────────────────────┐
│                    Data (Repositories)                      │
│   ActivityRepository / ItemRepository / CheckHistoryRepo    │
│   (interfaz)  →  *Impl (implementación con Room)             │
└──────────────┬───────────────────────────┬──────────────┘
               │                           │
   ┌───────────▼───────────┐   ┌───────────▼───────────┐
   │   Room (persistencia)  │   │   AIService (Strategy) │
   │  DAO + Entity + DB      │   │  interfaz + impl(s)     │
   └────────────────────────┘   └────────────────────────┘
```

**Regla de dependencia:** las capas de arriba conocen a las de abajo, nunca al revés. La UI no sabe que existe Room; el dominio no sabe que existe Compose. Esto es lo que permite, por ejemplo, cambiar de Room a otra base de datos, o de un `AIService` falso a uno real, sin tocar ViewModels ni pantallas.

## 2. Patrones de diseño usados y por qué

| Patrón | Dónde | Por qué |
|---|---|---|
| **Repository** | `data/repository` | Aísla el origen de los datos (Room) del resto de la app. El dominio solo conoce la interfaz. |
| **Strategy** | `ai/AIService` + implementaciones | El proveedor de IA (OpenAI, Gemini, Claude, o ninguno) es intercambiable sin cambiar el resto del código. Hoy usamos `LocalKeywordAIService` como estrategia por defecto. |
| **Dependency Injection (manual, sin framework)** | `di/AppContainer` | Las clases reciben sus dependencias por constructor en vez de crearlas ellas mismas (Inversión de Control). Se eligió un contenedor manual en vez de Hilt/Koin para mantener el proyecto simple y 100% explícito, dado el tamaño de la app. |
| **Use Case / Interactor** | `domain/usecase` | Cada acción de negocio ("generar checklist", "guardar actividad") es una clase con una sola función `invoke()`. Facilita testear la lógica sin UI ni base de datos real. |
| **Sealed classes como máquina de estados** | `viewmodel/UiState.kt`, `data/model/CheckStatus.kt` | Modelan estados mutuamente excluyentes (`Loading`, `Success`, `Error` / `Pending`, `Complete`, `Incomplete`) de forma que el compilador obliga a manejar todos los casos (`when` exhaustivo). Evita banderas booleanas ambiguas. |
| **Mapper (Entity ↔ Domain Model)** | `data/database/entity` → `data/model` | Las entidades de Room (con anotaciones `@Entity`) nunca se exponen fuera de la capa de datos; se convierten a modelos de dominio inmutables. Así Room puede cambiar sin romper el resto de la app. |
| **LRU Cache** | `data/repository/RecentActivitiesCache.kt` | Estructura de datos (`LinkedHashMap` con orden de acceso) que mantiene en memoria las últimas N actividades usadas, para que `HomeScreen` no tenga que ir siempre a disco — importante en un reloj con recursos limitados. |
| **Result/Resource wrapper** | `core/Result.kt` | En vez de lanzar excepciones que rompan la UI, las operaciones que pueden fallar (como llamar al `AIService`) devuelven un tipo `Result<T>` explícito (`Success` / `Failure`). |

## 3. Modelo de dominio

```text
Activity
 ├── id: Long
 ├── name: String
 ├── description: String
 └── items: List<Item>      (relación 1..N, cargada por el repositorio)

Item
 ├── id: Long
 ├── activityId: Long
 ├── name: String
 └── isChecked: Boolean

CheckHistory
 ├── id: Long
 ├── activityId: Long
 ├── timestamp: Long
 └── result: CheckResult      (COMPLETE | INCOMPLETE)
```

Estos son **modelos de dominio inmutables** (`data class` con `val`). Cualquier cambio de estado (marcar un ítem, editar el nombre) crea una copia nueva con `.copy(...)`, nunca se mutan en el sitio. Esto hace el estado predecible y fácil de razonar, algo especialmente valioso en una UI reactiva con Compose.

## 4. Estructuras de datos elegidas (y por qué)

* **`List<Item>` ordenada** para la checklist: el orden en que el usuario agrega los objetos importa para la UI, así que se prefiere `List` sobre `Set`.
* **`LinkedHashMap<Long, Activity>` con `accessOrder = true`** en `RecentActivitiesCache`: implementa una **caché LRU** (Least Recently Used) en O(1) para insertar/leer/expulsar, evitando lecturas repetidas a Room para las actividades que el usuario revisa seguido (patrón típico en apps de reloj, donde cada consulta a disco cuesta batería y tiempo).
* **`sealed class` en vez de `enum` cuando el estado lleva datos** (`UiState.Success(data)` vs `UiState.Error(message)`): un `enum` no puede cargar payload distinto por rama; una `sealed class` sí, y sigue siendo exhaustiva en el `when`.
* **`Map<Long, List<Item>>` en memoria** dentro de `ItemRepositoryImpl` para agrupar ítems por actividad tras una sola consulta a Room, evitando N+1 queries.

## 5. Flujo end-to-end (ejemplo: generar checklist con IA)

```text
ActivityScreen (UI)
   │ usuario escribe "Voy a acampar" y pulsa "Generar"
   ▼
ActivityViewModel.generateChecklist(description)
   │ emite UiState.Loading
   ▼
GenerateChecklistUseCase(description)
   │ delega en AIService.suggestItems(description)
   ▼
AIService (Strategy: LocalKeywordAIService por defecto)
   │ devuelve Result<List<Item>>
   ▼
ActivityViewModel
   │ Success → guarda vía ActivityRepository + ItemRepository
   │ Failure → emite UiState.Error("No se pudo generar la lista")
   ▼
ChecklistScreen observa el StateFlow y se recompone
```

## 6. Por qué NO se usó (decisiones explícitas)

* **Hilt/Koin:** un contenedor manual (`AppContainer`) es suficiente para ~10 clases y es más fácil de explicar/depurar en un proyecto académico.
* **Backend remoto / autenticación:** decisión ya tomada en el README original — todo es local (Room) salvo la llamada puntual al servicio de IA.
* **MVI completo (con Reducer/Intent):** MVVM + `StateFlow` + `sealed class UiState` da el mismo beneficio (estado unidireccional, inmutable) con menos ceremonia para el tamaño de esta app.

## 7. Cómo extender

* **Agregar un proveedor de IA real:** crear una clase que implemente `AIService` (por ejemplo `OpenAIService`, `GeminiService`) y cambiar una línea en `AppContainer`. Nada más se toca.
* **Agregar una nueva pantalla:** crear el Composable en `ui/`, su `ViewModel` si necesita estado propio, y registrarla en `DidIForgetNavGraph.kt`.
* **Agregar persistencia de un nuevo dato:** crear `Entity` + `Dao` en `data/database`, exponerlo por un `Repository`, y consumirlo desde un `UseCase`.
