package com.garbhyatra.app.data.local

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/** Aggregates tracker DAOs behind a simple API used by ViewModels. */
class TrackerRepository(
    private val waterDao: WaterDao,
    private val moodDao: MoodDao,
    private val journalDao: JournalDao,
    private val routineDao: RoutineDao
) {
    private fun today(): String = LocalDate.now().toString()

    fun observeWaterToday(): Flow<WaterEntity?> = waterDao.observe(today())

    suspend fun addGlass() {
        val date = today()
        val current = waterDao.get(date) ?: WaterEntity(date = date, glasses = 0)
        waterDao.upsert(current.copy(glasses = current.glasses + 1))
    }

    fun observeMoodToday(): Flow<MoodEntity?> = moodDao.observeForDate(today())

    suspend fun setMood(mood: String, note: String? = null) {
        moodDao.insert(MoodEntity(date = today(), mood = mood, note = note))
    }

    fun observeJournal(): Flow<List<JournalEntity>> = journalDao.observeAll()

    suspend fun addJournal(body: String, type: String = "gratitude") {
        if (body.isBlank()) return
        journalDao.insert(JournalEntity(date = today(), type = type, body = body))
    }

    fun observeRoutineToday(): Flow<List<RoutineCompletionEntity>> =
        routineDao.observeForDate(today())

    suspend fun toggleRoutine(taskKey: String, completed: Boolean) {
        val date = today()
        val key = "${date}_$taskKey"
        if (completed) {
            routineDao.insert(RoutineCompletionEntity(key = key, date = date, taskKey = taskKey))
        } else {
            routineDao.delete(key)
        }
    }
}
