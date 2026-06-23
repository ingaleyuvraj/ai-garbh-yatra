package com.garbhyatra.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

/** Notification channel + shared constants for reminders. */
object Notifications {
    const val CHANNEL_ID = "garbhyatra_reminders"

    const val EXTRA_TITLE = "extra_title"
    const val EXTRA_TEXT = "extra_text"
    const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    const val EXTRA_HOUR = "extra_hour"
    const val EXTRA_MINUTE = "extra_minute"

    // Request-code namespaces keep medication and reminder alarms from colliding.
    const val MED_REQUEST_BASE = 100_000
    const val REMINDER_REQUEST_BASE = 200_000

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java) ?: return
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "स्मरणपत्रे",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "गोळ्या, पाणी, नाश्ता आणि दिनचर्येची आठवण"
                }
                manager.createNotificationChannel(channel)
            }
        }
    }
}
