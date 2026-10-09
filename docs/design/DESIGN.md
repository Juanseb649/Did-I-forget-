# Especificación visual — "Did I Forget?"

Extraída del diseño de Claude Design (`Did-I-forget-System-Design.pdf` en esta misma carpeta; capturas por pantalla en `screens/`). Formato de referencia: Wear OS, 450×450.

## Paleta de colores

| Token | Valor | Uso |
|---|---|---|
| `background` | `#0A1013` (fondo "verde-petróleo profundo") | Fondo base de toda la app |
| `accent` (primary) | `#329C8A` | Botones primarios, elementos seleccionados, texto de acento |
| `accentBright` | `#3EA88F` aprox. | Círculo del ícono de éxito ("Todo listo") |
| `cardUnselected` | `#12181C` aprox. (gris muy oscuro) | Chips/filas no seleccionadas o inactivas |
| `cardSelected` | `#0E2420` aprox. (verde oscuro translúcido) + borde `accent` | Chips/filas seleccionadas o marcadas |
| `warning` (coral) | `#E07856` aprox. | Ícono y texto de "TE FALTA" |
| `warningBackgroundVignette` | Radial oscuro marrón/rojizo (`#2A1210` al centro → `background`) | Fondo de `ResultScreen` cuando falta algo |
| `successBackgroundVignette` | Radial oscuro verde (`#0F2A22` al centro → `background`) | Fondo de `ResultScreen` cuando todo está listo |
| `textPrimary` | Blanco / casi blanco | Títulos, nombres de ítems |
| `textSecondary` | Gris (`#8A9296` aprox.) | Subtítulos, contadores, hints |
| Categoría "Universidad" | Ámbar (`#E0A458` aprox.) | Ícono de graduación |
| Categoría "Trabajo" | Azul (`#5B8DEF` aprox.) | Ícono de maletín |
| Categoría "Viaje" | Lavanda (`#9B8AC4` aprox.), atenuado si no tiene objetos | Ícono de avión |

> Nota: los hex exactos de las categorías y vignettes son una aproximación visual (sacada de la imagen renderizada). El único hex confirmado explícitamente en el diseño es el `accent` (`#329C8A`). Si Claude Design permite inspeccionar cada capa, vale la pena confirmar los demás antes de fijarlos en `Color.kt`.

## Pantalla 1 — Home (`ui/home/HomeScreen.kt`)

- Arriba: hora del sistema ("10:24") — esto lo da gratis el `TimeText` de Wear Compose, no hay que construirlo a mano.
- Título: **"¿Qué vas a hacer?"**
- Lista de chips (de arriba a abajo):
  1. **"+ Nueva actividad"** — chip ancho completo, relleno sólido `accent`, texto oscuro.
  2. **"🎓 Universidad" / "4 objetos"** — ícono de graduación en ámbar, título blanco, subtítulo gris.
  3. **"💼 Trabajo" / "4 objetos"** — ícono de maletín en azul.
  4. **"✈️ Viaje"** — SIN subtítulo (actividad sin objetos todavía) y todo el chip atenuado/gris (ícono, texto y fondo con menor contraste) — es el estado "actividad vacía".

## Pantalla 2 — Nueva actividad (`ui/activity/ActivityScreen.kt`)

- Control segmentado arriba: **"✨ IA"** (pastilla `accent`, seleccionada) / **"✏️ Manual"** (texto gris, no seleccionada).
- Etiqueta pequeña en mayúsculas gris: **"DESCRIBE LA ACTIVIDAD"**.
- Campo de texto con contenido de ejemplo: *"Voy a acampar este fin de semana"* (con cursor visible).
- Botón ancho: **"✨ Generar lista"** — relleno `accent`.
- Texto de ayuda debajo, gris, pequeño: **"Toca para dictar por voz"**.

## Pantalla 3 — Selección de objetos (`ui/activity/ObjectSelectionScreen.kt`, NUEVA)

Pantalla que no existe todavía en el código: aparece después de generar/escribir la lista, antes de guardar. El usuario confirma qué objetos sugeridos realmente quiere guardar.

- Header: ícono de la actividad (aquí una carpa/triángulo, en ámbar) + nombre: **"Acampar"**.
- Grilla de **2 columnas × 3 filas**, un chip por objeto, cada uno con ícono arriba y nombre abajo:
  - Carpa (ícono de carpa) — **seleccionado**
  - Linterna (ícono de linterna) — **seleccionado**
  - Agua (ícono de gota) — **seleccionado**
  - Botiquín (ícono de botiquín) — no seleccionado
  - Ropa (ícono de percha/ropa) — **seleccionado**
  - Cargador (ícono de rayo) — no seleccionado
  - Seleccionado = borde + relleno `accent` tenue, ícono y texto en `accent`. No seleccionado = chip gris plano, ícono/texto atenuados.
- Botón: **"✓ Guardar (4)"** — el número refleja cuántos objetos están marcados en ese momento.

## Pantalla 4 — Revisar objetos / checklist (`ui/checklist/ChecklistScreen.kt`)

- Header: ícono de actividad + **"Acampar"**, subtítulo gris: **"3 de 4 verificados"**.
- Filas (icono + nombre a la izquierda, checkbox circular a la derecha):
  - Carpa, Linterna, Agua — fila con fondo `cardSelected`, checkbox `accent` con palomita.
  - Ropa — fila con fondo `cardUnselected`, checkbox vacío (solo el aro, sin relleno).
- Botón: **"Verificar"** — relleno `accent`.


## Pantalla 5 — Resultado: falta algo (`ui/result/ResultScreen.kt`, estado INCOMPLETE)

- Insignia circular con ícono de advertencia (triángulo con "!"), tono coral sobre un círculo rojo oscuro.
- Título: **"TE FALTA"** — coral, mayúsculas, con tracking.
- Fila del objeto faltante: ícono + **"Ropa"**.
- Texto gris: **"El reloj vibró para avisarte"**.
- Botón: **"Volver a la lista"** — relleno `accent`, vuelve a `ChecklistScreen`.
- Fondo: viñeta radial oscura en tonos marrón/rojizo (no negro plano).

## Pantalla 6 — Resultado: todo listo (`ui/result/ResultScreen.kt`, estado COMPLETE)

- Insignia circular sólida `accentBright` con palomita negra.
- Título: **"TODO LISTO"** — `accent`, mayúsculas, con tracking.
- Subtítulo grande blanco: **"Puedes salir"**.
- Texto gris pequeño: **"Acampar · 4 de 4 objetos"**.
- Botón: **"Listo"** — relleno `accent`, vuelve a `HomeScreen`.
- Fondo: viñeta radial oscura en tonos verdes (no negro plano).

## Iconografía necesaria (nueva)

El diseño depende de un ícono por objeto y por categoría de actividad. Hoy el código no tiene ese mapeo. Objetos vistos: carpa, linterna, gota/agua, botiquín, ropa/percha, rayo/cargador, birrete/graduación, maletín, avión, triángulo de advertencia, palomita. Se puede resolver con `Icons.Filled.*` de Material Icons Extended (ya trae equivalentes razonables para casi todos) más 2-3 íconos custom simples como vectores si algo no existe en la librería estándar.
