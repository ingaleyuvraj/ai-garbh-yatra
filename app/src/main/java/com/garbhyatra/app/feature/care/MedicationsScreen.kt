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
import androidx.compose.material3.Checkbox
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
import com.garbhyatra.app.ui.components.DisclaimerBanner
import com.garbhyatra.app.ui.components.SoftCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationsScreen(
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
                title = { Text(stringResource(R.string.care_meds_title)) },
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
            Text(stringResource(R.string.care_meds_subtitle), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            if (state.medications.isEmpty()) {
                SoftCard { Text(stringResource(R.string.care_meds_empty)) }
            }

            state.medications.forEach { med ->
                val taken = med.id in state.takenMedIds
                SoftCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = taken,
                            onCheckedChange = { viewModel.setMedicationTaken(med.id, it) }
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(med.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            val timeLabel = formatTime(med.hour, med.minute)
                            val reminderLabel = if (med.reminderEnabled)
                                stringResource(R.string.care_reminder_at, timeLabel)
                            else stringResource(R.string.care_no_reminder)
                            Text(reminderLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                if (taken) stringResource(R.string.care_taken_today) else stringResource(R.string.care_not_taken_today),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        IconButton(onClick = { viewModel.deleteMedication(med) }) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.care_delete))
                        }
                    }
                }
            }

            DisclaimerBanner(modifier = Modifier.fillMaxWidth())
        }
    }

    if (showAdd) {
        AddMedicationDialog(
            onDismiss = { showAdd = false },
            onConfirm = { name, hour, minute, reminder ->
                viewModel.addMedication(name, hour, minute, reminder)
                showAdd = false
            }
        )
    }
}

@Composable
private fun AddMedicationDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Int, Int, Boolean) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var hour by remember { mutableIntStateOf(9) }
    var minute by remember { mutableIntStateOf(0) }
    var reminder by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, hour, minute, reminder) },
                enabled = name.isNotBlank()
            ) { Text(stringResource(R.string.care_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.care_cancel)) }
        },
        title = { Text(stringResource(R.string.care_add_med)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.care_med_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.care_reminder_time), modifier = Modifier.weight(1f))
                    TextButton(onClick = {
                        TimePickerDialog(context, { _, h, m -> hour = h; minute = m }, hour, minute, true).show()
                    }) { Text(formatTime(hour, minute)) }
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.care_enable_reminder), modifier = Modifier.weight(1f))
                    Switch(checked = reminder, onCheckedChange = { reminder = it })
                }
            }
        }
    )
}
