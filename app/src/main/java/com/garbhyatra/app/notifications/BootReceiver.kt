package com.garbhyatra.app.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.garbhyatra.app.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Re-schedules all reminders after a device reboot (alarms are cleared on boot). */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val pending = goAsync()
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.get(appContext)
                db.reminderDao().getEnabled().forEach { r ->
                    ReminderScheduler.scheduleDaily(
                        appContext,
                        Notifications.REMINDER_REQUEST_BASE + r.id.toInt(),
                        r.hour,
                        r.minute,
                        r.label,
                        "गर्भयात्रा स्मरणपत्र"
                    )
                }
                db.medicationDao().getAll()
                    .filter { it.active && it.reminderEnabled }
                    .forEach { m ->
                        ReminderScheduler.scheduleDaily(
                            appContext,
                            Notifications.MED_REQUEST_BASE + m.id.toInt(),
                            m.hour,
                            m.minute,
                            m.name,
                            "गोळी घेण्याची वेळ झाली आहे"
                        )
                    }
            } finally {
                pending.finish()
            }
        }
    }
}
