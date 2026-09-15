package com.didiforget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.didiforget.ui.navigation.DidIForgetNavGraph
import com.didiforget.ui.theme.DidIForgetTheme

/**
 * Punto de entrada. Deliberadamente delgada: no contiene lógica de negocio,
 * solo monta el tema y el grafo de navegación. El [AppContainer] vive en
 * [DidIForgetApplication], no aquí, para sobrevivir a recreaciones de la
 * Activity (por ejemplo, un cambio de configuración).
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val container = (application as DidIForgetApplication).container

        setContent {
            DidIForgetTheme {
                DidIForgetNavGraph(container = container)
            }
        }
    }
}
