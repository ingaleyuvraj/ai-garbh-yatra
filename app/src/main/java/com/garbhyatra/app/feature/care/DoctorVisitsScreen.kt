package com.garbhyatra.app.feature.care

import android.app.DatePickerDialog
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.garbhyatra.app.R
import com.garbhyatra.app.di.AppContainer
import com.garbhyatra.app.ui.AppViewModelProvider
import com.garbhyatra.app.ui.components.SoftCard
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorVisitsScreen(
    container: AppContainer,
    onBack: () -> Unit,
    viewModel: CareViewModel = viewModel(factory = AppViewModelProvider.factory(container))
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showAdd by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.care_visits_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
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
            Text(stringResource(R.string.care_visits_subtitle), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            if (state.visits.isEmpty()) {
                SoftCard { Text(stringResource(R.string.care_visits_empty)) }
            }

            state.visits.forEach { visit ->
                SoftCard {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Checkbox(
                            checked = visit.done,
                            onCheckedChange = { viewModel.setVisitDone(visit.id, it) }
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                visit.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                textDecoration = if (visit.done) TextDecoration.LineThrough else TextDecoration.None
                            )
                            Text("📅  " + formatDateTime(visit.dateTimeEpochMillis), style = MaterialTheme.typography.bodyMedium)
                            visit.reason?.let { Text(stringResource(R.string.care_visit_reason_label, it), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            visit.notes?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        }
                        IconButton(onClick = { viewModel.deleteVisit(visit.id) }) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.care_delete))
                        }
                    }
                }
            }
        }
    }

    if (showAdd) {
        AddVisitDialog(
            onDismiss = { showAdd = false },
            onConfirm = { title, millis, reason, notes ->
                viewModel.addVisit(title, millis, reason, notes)
                showAdd = false
            }
        )
    }
}

private fun formatDateTime(millis: Long): String {
    val fmt = SimpleDateFormat("EEE, dd MMM yyyy • HH:mm", Locale("mr", "IN"))
    return fmt.format(millis)
}

@Composable
private fun AddVisitDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Long, String?, String?) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var dateTime by remember {
        mutableLongStateOf(
            Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 10)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onConfirm(title, dateTime, reason, notes) },
                enabled = title.isNotBlank()
            ) { Text(stringResource(R.string.care_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.care_cancel)) }
        },
        title = { Text(stringResource(R.string.care_add_visit)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.care_visit_title)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.care_visit_when), modifier = Modifier.weight(1f))
                    TextButton(onClick = {
                        val cal = Calendar.getInstance().apply { timeInMillis = dateTime }
                        DatePickerDialog(
                            context,
                            { _, y, mo, d ->
                                TimePickerDialog(
                                    context,
                                    { _, h, min ->
                                        dateTime = Calendar.getInstance().apply {
                                            set(y, mo, d, h, min, 0)
                                            set(Calendar.MILLISECOND, 0)
                                        }.timeInMillis
                                    },
                                    cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
                                ).show()
                            },
                            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }) { Text(formatDateTime(dateTime)) }
                }
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text(stringResource(R.string.care_visit_reason)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(stringResource(R.string.care_visit_notes)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
            }
        }
    )
}
