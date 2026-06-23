package com.garbhyatra.app.data.backup

import android.content.Context
import android.net.Uri
import com.garbhyatra.app.data.local.AppDatabase
import com.garbhyatra.app.data.prefs.UserPreferencesRepository
import com.garbhyatra.app.domain.model.Stage
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

/** Exports all user data to a JSON file and restores it back. */
class BackupRepository(
    private val context: Context,
    private val db: AppDatabase,
    private val prefs: UserPreferencesRepository
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    suspend fun buildBackup(): BackupData {
        val p = prefs.userPrefs.first()
        return BackupData(
            prefs = BackupPrefs(
                name = p.name,
                stage = p.stage.code,
                installEpochDay = p.installEpochDay,
                onboarded = p.onboarded,
                consent = p.consentAccepted
            ),
            water = db.waterDao().getAll(),
            mood = db.moodDao().getAll(),
            journal = db.journalDao().getAll(),
            routine = db.routineDao().getAll(),
            medications = db.medicationDao().getAll(),
            medicationLogs = db.medicationDao().getAllLogs(),
            reminders = db.reminderDao().getAll(),
            doctorVisits = db.doctorVisitDao().getAll()
        )
    }

    /** Serializes all data and writes it to [uri]. */
    suspend fun exportTo(uri: Uri) {
        val text = json.encodeToString(BackupData.serializer(), buildBackup())
        context.contentResolver.openOutputStream(uri)?.use { out ->
            out.write(text.toByteArray(Charsets.UTF_8))
            out.flush()
        } ?: error("Could not open file for writing")
    }

    /** Reads a backup file at [uri] and restores all data. */
    suspend fun importFrom(uri: Uri) {
        val text = context.contentResolver.openInputStream(uri)?.use { input ->
            input.readBytes().toString(Charsets.UTF_8)
        } ?: error("Could not open file for reading")
        val data = json.decodeFromString(BackupData.serializer(), text)
        restore(data)
    }

    private suspend fun restore(data: BackupData) {
        data.water.forEach { db.waterDao().upsert(it) }
        data.mood.forEach { db.moodDao().insert(it) }
        data.journal.forEach { db.journalDao().insert(it) }
        data.routine.forEach { db.routineDao().insert(it) }
        data.medications.forEach { db.medicationDao().upsert(it) }
        data.medicationLogs.forEach { db.medicationDao().upsertLog(it) }
        data.reminders.forEach { db.reminderDao().upsert(it) }
        data.doctorVisits.forEach { db.doctorVisitDao().upsert(it) }

        val bp = data.prefs
        if (bp.name.isNotBlank() || bp.onboarded) {
            prefs.restore(
                name = bp.name,
                stageCode = Stage.fromCode(bp.stage).code,
                installEpochDay = bp.installEpochDay,
                onboarded = bp.onboarded,
                consent = bp.consent
            )
        }
    }
}
