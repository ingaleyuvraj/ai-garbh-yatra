package com.garbhyatra.app.data.backup

import com.garbhyatra.app.data.local.DoctorVisitEntity
import com.garbhyatra.app.data.local.JournalEntity
import com.garbhyatra.app.data.local.MedicationEntity
import com.garbhyatra.app.data.local.MedicationLogEntity
import com.garbhyatra.app.data.local.MoodEntity
import com.garbhyatra.app.data.local.ReminderEntity
import com.garbhyatra.app.data.local.RoutineCompletionEntity
import com.garbhyatra.app.data.local.WaterEntity
import kotlinx.serialization.Serializable

/** Full export/import payload. All fields default to empty for forward compatibility. */
@Serializable
data class BackupData(
    val schema: Int = 2,
    val app: String = "garbhyatra",
    val exportedAt: Long = System.currentTimeMillis(),
    val prefs: BackupPrefs = BackupPrefs(),
    val water: List<WaterEntity> = emptyList(),
    val mood: List<MoodEntity> = emptyList(),
    val journal: List<JournalEntity> = emptyList(),
    val routine: List<RoutineCompletionEntity> = emptyList(),
    val medications: List<MedicationEntity> = emptyList(),
    val medicationLogs: List<MedicationLogEntity> = emptyList(),
    val reminders: List<ReminderEntity> = emptyList(),
    val doctorVisits: List<DoctorVisitEntity> = emptyList()
)

@Serializable
data class BackupPrefs(
    val name: String = "",
    val stage: String = "t1",
    val installEpochDay: Long = 0,
    val onboarded: Boolean = false,
    val consent: Boolean = false
)
