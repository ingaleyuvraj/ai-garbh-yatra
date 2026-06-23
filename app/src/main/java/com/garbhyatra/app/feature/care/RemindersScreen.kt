package com.garbhyatra.app.feature.care

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.garbhyatra.app.R
import com.garbhyatra.app.di.AppContainer
import com.garbhyatra.app.ui.AppViewModelProvider
import com.garbhyatra.app.ui.components.SoftCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    container: AppContainer,
    onBack: () -> Unit,
    viewModel: CareViewModel = viewModel(factory = AppViewModelProvider.factory(container))
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showAdd by remember { mutableStateOf(false) }
    val notifPermission = rememberNotificationPermission()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.care_reminders_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                notifPermission.request()
                showAdd = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.care_add))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(stringResource(R.string.care_reminders_subtitle), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            if (state.reminders.isEmpty()) {
                SoftCard { Text(stringResource(R.string.care_reminders_empty)) }
            }

            state.reminders.forEach { reminder ->
                SoftCard {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text(reminderEmoji(reminder.type), style = MaterialTheme.typography.titleLarge)
                        Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                            Text(reminder.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(formatTime(reminder.hour, reminder.minute), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = reminder.enabled,
                            onCheckedChange = { viewModel.setReminderEnabled(reminder, it) }
                        )
                        IconButton(onClick = { viewModel.deleteReminder(reminder) }) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.care_delete))
                        }
                    }
                }
            }
        }
    }

    if (showAdd) {
        AddReminderDialog(
            onDismiss = { showAdd = false },
            onConfirm = { type, label, hour, minute ->
                viewModel.addReminder(type, label, hour, minute)
                showAdd = false
            }
        )
    }
}

private fun reminderEmoji(type: String): String = when (type) {
    "water" -> "💧"
    "breakfast" -> "🍽"
    else -> "⏰"
}

@Composable
private fun AddReminderDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, Int) -> Unit
) {
    val context = LocalContext.current
    val waterLabel = stringResource(R.string.care_reminder_water)
    val breakfastLabel = stringResource(R.string.care_reminder_breakfast)

    var type by remember { mutableStateOf("water") }
    var label by remember { mutableStateOf(waterLabel) }
    var hour by remember { mutableIntStateOf(8) }
    var minute by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onConfirm(type, label, hour, minute) },
                enabled = label.isNotBlank()
            ) { Text(stringResource(R.string.care_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.care_cancel)) }
        },
        title = { Text(stringResource(R.string.care_add_reminder)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val options = listOf(
                        "water" to waterLabel,
                        "breakfast" to breakfastLabel,
                        "custom" to stringResource(R.string.care_reminder_custom)
                    )
                    options.forEach { (key, lbl) ->
                        AssistChip(
                            onClick = {
                                type = key
                                if (key != "custom") label = lbl else label = ""
                            },
                            label = { Text(lbl) },
                            colors = if (type == key)
                                AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            else AssistChipDefaults.assistChipColors()
                        )
                    }
                }
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text(stringResource(R.string.care_reminder_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.care_reminder_time), modifier = Modifier.weight(1f))
                    TextButton(onClick = {
                        TimePickerDialog(context, { _, h, m -> hour = h; minute = m }, hour, minute, true).show()
                    }) { Text(formatTime(hour, minute)) }
                }
            }
        }
    )
}
