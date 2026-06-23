package com.garbhyatra.app.di

import android.content.Context
import com.garbhyatra.app.data.backup.BackupRepository
import com.garbhyatra.app.data.content.ContentRepository
import com.garbhyatra.app.data.local.AppDatabase
import com.garbhyatra.app.data.local.CareRepository
import com.garbhyatra.app.data.local.TrackerRepository
import com.garbhyatra.app.data.prefs.UserPreferencesRepository
import com.garbhyatra.app.domain.routine.RoutineEngine

/**
 * Simple manual dependency container (no Hilt) — created once in the Application
 * and passed down to composables/ViewModels.
 */
class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    private val database: AppDatabase by lazy { AppDatabase.get(appContext) }

    val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(appContext)
    }

    val contentRepository: ContentRepository by lazy {
        ContentRepository(appContext)
    }

    val routineEngine: RoutineEngine by lazy {
        RoutineEngine(contentRepository)
    }

    val trackerRepository: TrackerRepository by lazy {
        TrackerRepository(database.waterDao(), database.moodDao(), database.journalDao(), database.routineDao())
    }

    val careRepository: CareRepository by lazy {
        CareRepository(appContext, database.medicationDao(), database.reminderDao(), database.doctorVisitDao())
    }

    val backupRepository: BackupRepository by lazy {
        BackupRepository(appContext, database, userPreferencesRepository)
    }
}
