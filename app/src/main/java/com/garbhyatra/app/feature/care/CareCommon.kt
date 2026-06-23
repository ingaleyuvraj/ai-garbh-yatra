package com.garbhyatra.app.feature.care

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/** Formats a 24-hour clock time as HH:mm. */
fun formatTime(hour: Int, minute: Int): String = "%02d:%02d".format(hour, minute)

/** Handle for requesting the POST_NOTIFICATIONS runtime permission (API 33+). */
class NotificationPermissionState(private val onRequest: () -> Unit) {
    fun request() = onRequest()
}

@Composable
fun rememberNotificationPermission(): NotificationPermissionState {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* result ignored: reminders simply won't show if denied */ }

    return remember {
        NotificationPermissionState {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val granted = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                if (!granted) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
