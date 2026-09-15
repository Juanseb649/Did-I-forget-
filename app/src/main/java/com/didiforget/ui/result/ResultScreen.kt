package com.didiforget.ui.result

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.didiforget.R
import com.didiforget.data.model.CheckResult
import com.didiforget.viewmodel.ChecklistViewModel

/**
 * Pantalla de resultado (README, "Flujo de usuario" paso 5): muestra si falta
 * algo o si "todo listo", y hace vibrar el reloj cuando falta algo, como pide
 * el README ("El smartwatch puede utilizar vibración/haptic feedback").
 */
@Composable
fun ResultScreen(
    viewModel: ChecklistViewModel,
    modifier: Modifier = Modifier
) {
    val lastCheck by viewModel.lastCheck.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(lastCheck?.result) {
        if (lastCheck?.result == CheckResult.INCOMPLETE) {
            vibrate(context)
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (lastCheck?.result) {
            CheckResult.COMPLETE -> {
                Text(
                    text = stringResource(R.string.result_all_ready_title),
                    color = MaterialTheme.colors.primary
                )
                Text(text = stringResource(R.string.result_all_ready_subtitle))
            }

            CheckResult.INCOMPLETE -> {
                Text(
                    text = stringResource(R.string.result_missing_title),
                    color = MaterialTheme.colors.error
                )
                lastCheck?.missingItemNames?.forEach { name -> Text(text = name) }
            }

            null -> Text("Verificando…")
        }
    }
}

/** Feedback háptico simple; usa la API vigente según la versión de Android. */
private fun vibrate(context: Context) {
    val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(300)
    }
}
