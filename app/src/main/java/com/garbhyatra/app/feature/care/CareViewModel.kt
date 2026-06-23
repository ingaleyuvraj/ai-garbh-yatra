package com.garbhyatra.app.feature.care

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garbhyatra.app.data.local.CareRepository
import com.garbhyatra.app.data.local.DoctorVisitEntity
import com.garbhyatra.app.data.local.MedicationEntity
import com.garbhyatra.app.data.local.ReminderEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CareUiState(
    val medications: List<MedicationEntity> = emptyList(),
    val takenMedIds: Set<Long> = emptySet(),
    val reminders: List<ReminderEntity> = emptyList(),
    val visits: List<DoctorVisitEntity> = emptyList()
)

class CareViewModel(
    private val repository: CareRepository
) : ViewModel() {

    val uiState: StateFlow<CareUiState> = combine(
        repository.observeMedications(),
        repository.observeMedLogToday(),
        repository.observeReminders(),
        repository.observeVisits()
    ) { meds, logs, reminders, visits ->
        CareUiState(
            medications = meds,
            takenMedIds = logs.filter { it.taken }.map { it.medId }.toSet(),
            reminders = reminders,
            visits = visits
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CareUiState()
    )

    fun addMedication(name: String, hour: Int, minute: Int, reminderEnabled: Boolean) {
        viewModelScope.launch { repository.addMedication(name, hour, minute, reminderEnabled) }
    }

    fun setMedicationTaken(medId: Long, taken: Boolean) {
        viewModelScope.launch { repository.setMedicationTaken(medId, taken) }
    }

    fun deleteMedication(med: MedicationEntity) {
        viewModelScope.launch { repository.deleteMedication(med) }
    }

    fun addReminder(type: String, label: String, hour: Int, minute: Int) {
        viewModelScope.launch { repository.addReminder(type, label, hour, minute) }
    }

    fun setReminderEnabled(reminder: ReminderEntity, enabled: Boolean) {
        viewModelScope.launch { repository.setReminderEnabled(reminder, enabled) }
    }

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch { repository.deleteReminder(reminder) }
    }

    fun addVisit(title: String, dateTimeEpochMillis: Long, reason: String?, notes: String?) {
        viewModelScope.launch { repository.addVisit(title, dateTimeEpochMillis, reason, notes) }
    }

    fun setVisitDone(id: Long, done: Boolean) {
        viewModelScope.launch { repository.setVisitDone(id, done) }
    }

    fun deleteVisit(id: Long) {
        viewModelScope.launch { repository.deleteVisit(id) }
    }
}
