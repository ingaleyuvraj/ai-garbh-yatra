package com.garbhyatra.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "water")
data class WaterEntity(
    @PrimaryKey val date: String, // ISO yyyy-MM-dd
    val glasses: Int = 0,
    val goal: Int = 8
)

@Serializable
@Entity(tableName = "mood")
data class MoodEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val mood: String,
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
@Entity(tableName = "journal")
data class JournalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val type: String = "gratitude",
    val body: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
@Entity(tableName = "routine_completion")
data class RoutineCompletionEntity(
    @PrimaryKey val key: String, // date + "_" + taskKey
    val date: String,
    val taskKey: String,
    val completedAt: Long = System.currentTimeMillis()
)

/** A tablet/supplement the user takes (e.g. फॉलिक अ‍ॅसिड, लोह). */
@Serializable
@Entity(tableName = "medication")
data class MedicationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val hour: Int = 9,
    val minute: Int = 0,
    val reminderEnabled: Boolean = true,
    val active: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

/** Daily taken/not-taken record for a medication. */
@Serializable
@Entity(tableName = "medication_log")
data class MedicationLogEntity(
    @PrimaryKey val key: String, // date + "_" + medId
    val date: String,
    val medId: Long,
    val taken: Boolean = true,
    val takenAt: Long = System.currentTimeMillis()
)

/** A generic time-of-day reminder (water, breakfast, custom). */
@Serializable
@Entity(tableName = "reminder")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String = "custom", // water | breakfast | custom
    val label: String,
    val hour: Int = 8,
    val minute: Int = 0,
    val enabled: Boolean = true
)

/** A doctor appointment / visit. */
@Serializable
@Entity(tableName = "doctor_visit")
data class DoctorVisitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val dateTimeEpochMillis: Long,
    val reason: String? = null,
    val notes: String? = null,
    val done: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
