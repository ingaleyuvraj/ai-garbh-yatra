package com.garbhyatra.app.data.local

import android.content.Context
import com.garbhyatra.app.notifications.Notifications
import com.garbhyatra.app.notifications.ReminderScheduler
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Manages medications, time-of-day reminders and doctor visits, including scheduling
 * and cancelling the associated notification alarms.
 */
class CareRepository(
    private val context: Context,
    private val medicationDao: MedicationDao,
    private val reminderDao: ReminderDao,
    private val doctorVisitDao: DoctorVisitDao
) {
    private fun today(): String = LocalDate.now().toString()

    // ---- Medications ----
    fun observeMedications(): Flow<List<MedicationEntity>> = medicationDao.observeActive()
    fun observeMedLogToday(): Flow<List<MedicationLogEntity>> = medicationDao.observeLogForDate(today())

    suspend fun addMedication(name: String, hour: Int, minute: Int, reminderEnabled: Boolean) {
        if (name.isBlank()) return
        val id = medicationDao.upsert(
            MedicationEntity(name = name.trim(), hour = hour, minute = minute, reminderEnabled = reminderEnabled)
        )
        if (reminderEnabled) {
            ReminderScheduler.scheduleDaily(
                context, Notifications.MED_REQUEST_BASE + id.toInt(), hour, minute,
                name.trim(), "गोळी घेण्याची वेळ झाली आहे"
            )
        }
    }

    suspend fun setMedicationTaken(medId: Long, taken: Boolean) {
        val key = "${today()}_$medId"
        if (taken) {
            medicationDao.upsertLog(MedicationLogEntity(key = key, date = today(), medId = medId, taken = true))
        } else {
            medicationDao.deleteLog(key)
        }
    }

    suspend fun deleteMedication(med: MedicationEntity) {
        medicationDao.deactivate(med.id)
        ReminderScheduler.cancel(context, Notifications.MED_REQUEST_BASE + med.id.toInt())
    }

    // ---- Reminders ----
    fun observeReminders(): Flow<List<ReminderEntity>> = reminderDao.observeAll()

    suspend fun addReminder(type: String, label: String, hour: Int, minute: Int) {
        if (label.isBlank()) return
        val id = reminderDao.upsert(
            ReminderEntity(type = type, label = label.trim(), hour = hour, minute = minute, enabled = true)
        )
        ReminderScheduler.scheduleDaily(
            context, Notifications.REMINDER_REQUEST_BASE + id.toInt(), hour, minute,
            label.trim(), "गर्भयात्रा स्मरणपत्र"
        )
    }

    suspend fun setReminderEnabled(reminder: ReminderEntity, enabled: Boolean) {
        reminderDao.upsert(reminder.copy(enabled = enabled))
        val code = Notifications.REMINDER_REQUEST_BASE + reminder.id.toInt()
        if (enabled) {
            ReminderScheduler.scheduleDaily(context, code, reminder.hour, reminder.minute, reminder.label, "गर्भयात्रा स्मरणपत्र")
        } else {
            ReminderScheduler.cancel(context, code)
        }
    }

    suspend fun deleteReminder(reminder: ReminderEntity) {
        ReminderScheduler.cancel(context, Notifications.REMINDER_REQUEST_BASE + reminder.id.toInt())
        reminderDao.delete(reminder.id)
    }

    // ---- Doctor visits ----
    fun observeVisits(): Flow<List<DoctorVisitEntity>> = doctorVisitDao.observeAll()

    suspend fun addVisit(title: String, dateTimeEpochMillis: Long, reason: String?, notes: String?) {
        if (title.isBlank()) return
        doctorVisitDao.upsert(
            DoctorVisitEntity(
                title = title.trim(),
                dateTimeEpochMillis = dateTimeEpochMillis,
                reason = reason?.takeIf { it.isNotBlank() },
                notes = notes?.takeIf { it.isNotBlank() }
            )
        )
    }

    suspend fun setVisitDone(id: Long, done: Boolean) = doctorVisitDao.setDone(id, done)

    suspend fun deleteVisit(id: Long) = doctorVisitDao.delete(id)
}
