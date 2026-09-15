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
