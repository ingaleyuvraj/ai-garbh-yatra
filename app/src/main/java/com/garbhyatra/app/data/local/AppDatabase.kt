package com.garbhyatra.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        WaterEntity::class,
        MoodEntity::class,
        JournalEntity::class,
        RoutineCompletionEntity::class,
        MedicationEntity::class,
        MedicationLogEntity::class,
        ReminderEntity::class,
        DoctorVisitEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun waterDao(): WaterDao
    abstract fun moodDao(): MoodDao
    abstract fun journalDao(): JournalDao
    abstract fun routineDao(): RoutineDao
    abstract fun medicationDao(): MedicationDao
    abstract fun reminderDao(): ReminderDao
    abstract fun doctorVisitDao(): DoctorVisitDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "garbhyatra.db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
    }
}
