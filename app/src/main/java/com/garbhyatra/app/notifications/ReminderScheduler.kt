package com.garbhyatra.app.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

/** Schedules and cancels daily repeating reminder alarms via [AlarmManager]. */
object ReminderScheduler {

    /** Window length the OS may use to batch the alarm (keeps reminders punctual). */
    private const val WINDOW_MILLIS = 10 * 60 * 1000L

    private fun alarmManager(context: Context): AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private fun pendingIntent(
        context: Context,
        requestCode: Int,
        hour: Int,
        minute: Int,
        title: String,
        text: String
    ): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(Notifications.EXTRA_TITLE, title)
            putExtra(Notifications.EXTRA_TEXT, text)
            putExtra(Notifications.EXTRA_NOTIFICATION_ID, requestCode)
            putExtra(Notifications.EXTRA_HOUR, hour)
            putExtra(Notifications.EXTRA_MINUTE, minute)
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    /** Next occurrence (today if still ahead, else tomorrow) of the given clock time. */
    private fun nextTriggerMillis(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (target.timeInMillis <= now.timeInMillis) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis
    }

    /**
     * Schedules the next occurrence of a daily reminder. [requestCode] must be stable
     * per reminder. The reminder re-schedules itself for the following day when it fires
     * (see [ReminderReceiver]), so this stays punctual without the exact-alarm permission.
     */
    fun scheduleDaily(
        context: Context,
        requestCode: Int,
        hour: Int,
        minute: Int,
        title: String,
        text: String
    ) {
        val pi = pendingIntent(context, requestCode, hour, minute, title, text)
        alarmManager(context).setWindow(
            AlarmManager.RTC_WAKEUP,
            nextTriggerMillis(hour, minute),
            WINDOW_MILLIS,
            pi
        )
    }

    fun cancel(context: Context, requestCode: Int) {
        val pi = pendingIntent(context, requestCode, 0, 0, "", "")
        alarmManager(context).cancel(pi)
        pi.cancel()
    }
}
