package com.didiforget

import android.app.Application
import com.didiforget.di.AppContainer

/**
 * `Application` custom: crea el [AppContainer] una sola vez, con el
 * `applicationContext`, y lo mantiene vivo mientras la app viva. Las
 * `Activity`/Composables lo leen a través de `(application as
 * DidIForgetApplication).container`.
 */
class DidIForgetApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
