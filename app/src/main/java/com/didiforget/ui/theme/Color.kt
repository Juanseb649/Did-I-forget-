package com.didiforget.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta simple y de alto contraste: en un reloj, con luz solar directa y
// pantallas pequeñas, prima la legibilidad sobre la sutileza.
val DidIForgetPrimary = Color(0xFF4ADE9C) // verde: "todo listo"
val DidIForgetPrimaryVariant = Color(0xFF34B27B)
val DidIForgetSecondary = Color(0xFFFFB74D) // ámbar: advertencia / falta algo
val DidIForgetError = Color(0xFFFF6B5B)
val DidIForgetBackground = Color(0xFF000000) // AMOLED: negro puro ahorra batería
val DidIForgetSurface = Color(0xFF1B1D1F)
val DidIForgetOnPrimary = Color(0xFF00220F)
val DidIForgetOnSurface = Color(0xFFFFFFFF)

// Tokens adicionales usados por los componentes de ui/components (no forman
// parte de androidx.wear.compose.material.Colors porque esa clase no tiene
// slots para "superficie alternativa" o "texto secundario").
val DidIForgetSurfaceVariant = Color(0xFF2A2D2F)
val DidIForgetOnSurfaceMuted = Color(0xFFA0A6AA)
val DidIForgetPrimaryTint = Color(0xFF16261F) // fondo tenue para chips/íconos "seleccionados"
val DidIForgetErrorGlowStart = Color(0xFF3A211D) // centro del degradado en ResultScreen (falta algo)
val DidIForgetSuccessGlowStart = Color(0xFF15301F) // centro del degradado en ResultScreen (todo listo)
