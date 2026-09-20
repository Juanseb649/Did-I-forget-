package com.didiforget.ui.result

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.wear.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.didiforget.R
import com.didiforget.data.model.CheckResult
import com.didiforget.ui.components.PrimaryButton
import com.didiforget.ui.components.StatusResultScreen
import com.didiforget.ui.icons.visualForItem
import com.didiforget.ui.theme.DidIForgetError
import com.didiforget.ui.theme.DidIForgetErrorGlowEnd
import com.didiforget.ui.theme.DidIForgetErrorGlowMid
import com.didiforget.ui.theme.DidIForgetErrorGlowStart
import com.didiforget.ui.theme.DidIForgetOnPrimary
import com.didiforget.ui.theme.DidIForgetOnSurface
import com.didiforget.ui.theme.DidIForgetPrimary
import com.didiforget.ui.theme.DidIForgetPrimaryLight
import com.didiforget.ui.theme.DidIForgetSuccessGlowEnd
import com.didiforget.ui.theme.DidIForgetSuccessGlowMid
import com.didiforget.ui.theme.DidIForgetSuccessGlowStart
import com.didiforget.ui.theme.DidIForgetSurface
import com.didiforget.viewmodel.ChecklistViewModel
import com.didiforget.viewmodel.UiState

/**
 * Pantalla de resultado (DESIGN.md §4.4 / README, "Flujo de usuario" paso 5):
 * muestra si falta algo o si "todo listo", y hace vibrar el reloj — patrón
 * corto doble si falta algo, único si todo está listo.
 */
@Composable
fun ResultScreen(
    viewModel: ChecklistViewModel,
    modifier: Modifier = Modifier,
    onBackToList: () -> Unit = {},
    onDone: () -> Unit = {}
) {
    val lastCheck by viewModel.lastCheck.collectAsState()
    val state by viewModel.uiState.collectAsState()
    val activity = (state as? UiState.Success)?.data
    val context = LocalContext.current

    LaunchedEffect(lastCheck?.result) {
        when (lastCheck?.result) {
            CheckResult.INCOMPLETE -> vibrate(context, doublePulse = true)
            CheckResult.COMPLETE -> vibrate(context, doublePulse = false)
            null -> Unit
        }
    }

    when (lastCheck?.result) {
        CheckResult.COMPLETE -> StatusResultScreen(
            modifier = modifier,
            icon = R.drawable.ic_check,
            iconTint = DidIForgetOnPrimary,
            badgeSize = 72.dp,
            iconSize = 40.dp,
            badgeBackground = DidIForgetPrimary,
            glowStart = DidIForgetSuccessGlowStart,
            glowMid = DidIForgetSuccessGlowMid,
            glowEnd = DidIForgetSuccessGlowEnd,
            eyebrow = stringResource(R.string.result_all_ready_title),
            eyebrowColor = DidIForgetPrimaryLight,
            title = stringResource(R.string.result_all_ready_subtitle),
            titleStyle = MaterialTheme.typography.display1,
            caption = activity?.let {
                stringResource(R.string.result_summary_caption, it.name, it.checkedItems, it.totalItems)
            },
            footer = {
                PrimaryButton(text = stringResource(R.string.result_done_button), onClick = onDone)
            }
        )

        CheckResult.INCOMPLETE -> StatusResultScreen(
            modifier = modifier,
            icon = R.drawable.ic_alert_triangle,
            iconTint = DidIForgetError,
            badgeSize = 56.dp,
            iconSize = 30.dp,
            badgeBackground = DidIForgetError.copy(alpha = 0.16f),
            glowStart = DidIForgetErrorGlowStart,
            glowMid = DidIForgetErrorGlowMid,
            glowEnd = DidIForgetErrorGlowEnd,
            eyebrow = stringResource(R.string.result_missing_title),
            eyebrowColor = DidIForgetError,
            caption = stringResource(R.string.result_vibration_caption),
            content = {
                Spacer(modifier = Modifier.height(6.dp))
                lastCheck?.missingItemNames.orEmpty().forEach { name ->
                    MissingItemPill(name = name)
                }
            },
            footer = {
                PrimaryButton(text = stringResource(R.string.result_back_button), onClick = onBackToList)
            }
        )

        null -> Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.result_verifying))
        }
    }
}

/** Píldora "ícono + nombre" para cada objeto faltante (DESIGN.md, Pantalla 5). */
@Composable
private fun MissingItemPill(name: String, modifier: Modifier = Modifier) {
    val visual = visualForItem(name)
    Row(
        modifier = modifier
            .padding(top = 6.dp)
            .background(DidIForgetSurface, RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(visual.icon),
            contentDescription = null,
            tint = visual.tint,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = name, style = MaterialTheme.typography.title2, color = DidIForgetOnSurface)
    }
}

/** Feedback háptico; usa la API vigente según la versión de Android. */
private fun vibrate(context: Context, doublePulse: Boolean) {
    val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val effect = if (doublePulse) {
            VibrationEffect.createWaveform(longArrayOf(0, 80, 60, 80), -1)
        } else {
            VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE)
        }
        vibrator.vibrate(effect)
    } else {
        @Suppress("DEPRECATION")
        if (doublePulse) vibrator.vibrate(longArrayOf(0, 80, 60, 80), -1) else vibrator.vibrate(120)
    }
}
