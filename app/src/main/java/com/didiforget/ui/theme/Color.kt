package com.didiforget.ui.theme

import androidx.compose.ui.graphics.Color

// ── Marca ──────────────────────────────────────────────────────────────────
val DidIForgetPrimary = Color(0xFF329C8A) // acento único: TODOS los botones
val DidIForgetPrimaryVariant = Color(0xFF26786A) // pressed / bordes
val DidIForgetPrimaryLight = Color(0xFF4FBFAA) // iconos y texto sobre fondo oscuro
val DidIForgetOnPrimary = Color(0xFF04221D) // texto/ícono DENTRO de un botón

// ── Superficies (AMOLED-friendly: casi negro, nunca gris claro) ────────────
val DidIForgetBackground = Color(0xFF0A1417)
val DidIForgetSurface = Color(0xFF17262B) // tarjetas, chips no seleccionados
val DidIForgetSurfaceVariant = Color(0xFF23383D) // bordes de 1.5 dp, divisores
val DidIForgetPrimaryTint = Color(0x29329C8A) // 16% alpha: chip SELECCIONADO

// ── Texto ──────────────────────────────────────────────────────────────────
val DidIForgetOnSurface = Color(0xFFEEF6F5) // 15.4:1 sobre Background
val DidIForgetOnSurfaceMuted = Color(0xFF8EA4A8) //  6.1:1 sobre Background
val DidIForgetOnSurfaceDim = Color(0xFF5E747A) //  3.4:1 — solo eyebrows/12sp+bold

// ── Estado ─────────────────────────────────────────────────────────────────
val DidIForgetError = Color(0xFFE86A5E) // "te falta"
val DidIForgetSecondary = Color(0xFFE8A33D) // ámbar: advertencia / actividad
val DidIForgetErrorGlowStart = Color(0xFF3B2216) // centro degradado "te falta"
val DidIForgetErrorGlowMid = Color(0xFF170F0E)
val DidIForgetErrorGlowEnd = Color(0xFF0A0707)
val DidIForgetSuccessGlowStart = Color(0xFF16463D) // centro degradado "todo listo"
val DidIForgetSuccessGlowMid = Color(0xFF0A1F1C)
val DidIForgetSuccessGlowEnd = Color(0xFF06100E)

// ── Acentos de categoría (SOLO para iconos, nunca para fondos ni botones) ──
val AccentAmber = Color(0xFFE8A33D) // estudio / actividad / advertencia
val AccentBlue = Color(0xFF7FB2E8) // trabajo / documentos / tecnología
val AccentLilac = Color(0xFFC79BE8) // viaje / ocio
val AccentTeal = Color(0xFF4FBFAA) // salud / agua / seleccionado
