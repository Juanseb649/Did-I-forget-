package com.didiforget.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.DeviceFontFamilyName
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Typography

/**
 * Google Sans es fuente del sistema en Wear OS. `Font(DeviceFontFamilyName(...))`
 * la resuelve en runtime y degrada sola a la sans-serif del sistema si el
 * dispositivo no la tiene (requiere API 31+; el fallback cubre versiones menores).
 */
private val GoogleSans = FontFamily(
    Font(DeviceFontFamilyName("google-sans"), weight = FontWeight.Normal),
    Font(DeviceFontFamilyName("google-sans-medium"), weight = FontWeight.Medium),
    Font(DeviceFontFamilyName("google-sans-bold"), weight = FontWeight.Bold)
)

val DidIForgetTypography = Typography(
    display1 = TextStyle(fontFamily = GoogleSans, fontWeight = FontWeight.Bold, fontSize = 30.sp, lineHeight = 34.sp),
    title1 = TextStyle(fontFamily = GoogleSans, fontWeight = FontWeight.Medium, fontSize = 19.sp, lineHeight = 22.sp),
    title2 = TextStyle(fontFamily = GoogleSans, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 19.sp),
    body1 = TextStyle(fontFamily = GoogleSans, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 19.sp),
    body2 = TextStyle(fontFamily = GoogleSans, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 18.sp),
    button = TextStyle(fontFamily = GoogleSans, fontWeight = FontWeight.Medium, fontSize = 15.sp, letterSpacing = 0.sp),
    caption1 = TextStyle(fontFamily = GoogleSans, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 15.sp),
    caption2 = TextStyle(fontFamily = GoogleSans, fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 1.2.sp)
)
