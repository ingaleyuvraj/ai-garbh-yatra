package com.garbhyatra.app.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.garbhyatra.app.MainActivity
import com.garbhyatra.app.R

/** Fires when a reminder alarm triggers; posts the notification. */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Notifications.ensureChannel(context)

        val title = intent.getStringExtra(Notifications.EXTRA_TITLE) ?: context.getString(R.string.app_name)
        val text = intent.getStringExtra(Notifications.EXTRA_TEXT).orEmpty()
        val notificationId = intent.getIntExtra(Notifications.EXTRA_NOTIFICATION_ID, 1)
        val hour = intent.getIntExtra(Notifications.EXTRA_HOUR, -1)
        val minute = intent.getIntExtra(Notifications.EXTRA_MINUTE, -1)

        val contentIntent = PendingIntent.getActivity(
            context,
            notificationId,
            Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, Notifications.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        }

        // Re-schedule for the next day (keeps the daily reminder going punctually).
        if (hour in 0..23 && minute in 0..59) {
            ReminderScheduler.scheduleDaily(context, notificationId, hour, minute, title, text)
        }
    }
}
