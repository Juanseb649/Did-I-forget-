package com.didiforget.ui.result

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.wear.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.didiforget.R
import com.didiforget.data.model.CheckResult
import com.didiforget.ui.components.AlertBadge
import com.didiforget.ui.components.PrimaryButton
import com.didiforget.ui.components.PrimaryIconButton
import com.didiforget.ui.components.SuccessBadge
import com.didiforget.ui.components.StatusResultScreen
import com.didiforget.ui.icons.visualForItem
import com.didiforget.ui.theme.DidIForgetError
import com.didiforget.ui.theme.DidIForgetErrorGlowEnd
import com.didiforget.ui.theme.DidIForgetErrorGlowMid
import com.didiforget.ui.theme.DidIForgetErrorGlowStart
import com.didiforget.ui.theme.DidIForgetOnSurface
import com.didiforget.ui.theme.DidIForgetOnSurfaceMuted
import com.didiforget.ui.theme.DidIForgetPrimary
import com.didiforget.ui.theme.DidIForgetSuccessGlowEnd
import com.didiforget.ui.theme.DidIForgetSuccessGlowMid
import com.didiforget.ui.theme.DidIForgetSuccessGlowStart
import com.didiforget.viewmodel.ChecklistViewModel
import com.didiforget.viewmodel.UiState

private const val MAX_MISSING_SHOWN = 2

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
    // "Todo listo": el texto aparece solo cuando el visto termina de trazarse.
    var checkDrawn by remember(lastCheck) { mutableStateOf(false) }

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
            badge = { SuccessBadge(onCheckDrawn = { checkDrawn = true }) },
            textVisible = checkDrawn,
            glowStart = DidIForgetSuccessGlowStart,
            glowMid = DidIForgetSuccessGlowMid,
            glowEnd = DidIForgetSuccessGlowEnd,
            eyebrow = stringResource(R.string.result_all_ready_title),
            eyebrowColor = DidIForgetPrimary,
            eyebrowStyle = resultEyebrowStyle(),
            title = stringResource(R.string.result_all_ready_subtitle),
            titleStyle = MaterialTheme.typography.display1.copy(fontSize = 18.sp, lineHeight = 22.sp),
            caption = activity?.let {
                stringResource(R.string.result_summary_caption, it.name, it.checkedItems, it.totalItems)
            },
            footer = {
                PrimaryButton(
                    text = stringResource(R.string.result_done_button),
                    onClick = onDone,
                    modifier = Modifier.width(112.dp)
                )
            }
        )

        CheckResult.INCOMPLETE -> StatusResultScreen(
            modifier = modifier,
            badge = { AlertBadge() },
            glowStart = DidIForgetErrorGlowStart,
            glowMid = DidIForgetErrorGlowMid,
            glowEnd = DidIForgetErrorGlowEnd,
            eyebrow = stringResource(R.string.result_missing_title),
            eyebrowColor = DidIForgetError,
            eyebrowStyle = resultEyebrowStyle(),
            content = {
                Spacer(modifier = Modifier.height(6.dp))
                val missing = lastCheck?.missingItemNames.orEmpty()
                missing.take(MAX_MISSING_SHOWN).forEach { name -> MissingItemRow(name = name) }
                if (missing.size > MAX_MISSING_SHOWN) {
                    Text(
                        text = stringResource(R.string.result_more_items, missing.size - MAX_MISSING_SHOWN),
                        style = MaterialTheme.typography.caption1,
                        color = DidIForgetOnSurfaceMuted
                    )
                }
            },
            footer = {
                PrimaryIconButton(
                    icon = R.drawable.ic_chevron_left,
                    contentDescription = stringResource(R.string.result_back_button),
                    onClick = onBackToList
                )
            }
        )

        null -> Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.result_verifying))
        }
    }
}

/**
 * Objeto faltante (boceto HTML `.pending`): ícono + nombre en texto semibold,
 * sin píldora de fondo. Se muestran máximo [MAX_MISSING_SHOWN]; el resto va como "+N más".
 */
@Composable
private fun MissingItemRow(name: String, modifier: Modifier = Modifier) {
    val visual = visualForItem(name)
    Row(
        modifier = modifier.padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(visual.icon),
            contentDescription = null,
            tint = DidIForgetOnSurface,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.title2.copy(fontWeight = FontWeight.SemiBold),
            color = DidIForgetOnSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// Boceto HTML: .display1 21 px con tracking .16em, escalado a sp.
@Composable
private fun resultEyebrowStyle() = MaterialTheme.typography.caption2.copy(
    fontWeight = FontWeight.Bold,
    fontSize = 16.sp,
    lineHeight = 19.sp,
    letterSpacing = 0.16.em
)

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
