package com.garbhyatra.app

import android.app.Application
import com.garbhyatra.app.di.AppContainer
import com.garbhyatra.app.notifications.Notifications

/**
 * Application entry point. Holds the manual DI container.
 */
class GarbhyatraApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        Notifications.ensureChannel(this)
    }
}
