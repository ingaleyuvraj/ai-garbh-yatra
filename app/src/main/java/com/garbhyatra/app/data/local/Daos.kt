package com.garbhyatra.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterDao {
    @Query("SELECT * FROM water WHERE date = :date LIMIT 1")
    fun observe(date: String): Flow<WaterEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: WaterEntity)

    @Query("SELECT * FROM water WHERE date = :date LIMIT 1")
    suspend fun get(date: String): WaterEntity?

    @Query("SELECT * FROM water")
    suspend fun getAll(): List<WaterEntity>
}

@Dao
interface MoodDao {
    @Query("SELECT * FROM mood ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<MoodEntity>>

    @Query("SELECT * FROM mood WHERE date = :date ORDER BY createdAt DESC LIMIT 1")
    fun observeForDate(date: String): Flow<MoodEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MoodEntity)

    @Query("SELECT * FROM mood")
    suspend fun getAll(): List<MoodEntity>
}

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<JournalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: JournalEntity)

    @Query("SELECT * FROM journal")
    suspend fun getAll(): List<JournalEntity>
}

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routine_completion WHERE date = :date")
    fun observeForDate(date: String): Flow<List<RoutineCompletionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: RoutineCompletionEntity)

    @Query("DELETE FROM routine_completion WHERE key = :key")
    suspend fun delete(key: String)

    @Query("SELECT * FROM routine_completion")
    suspend fun getAll(): List<RoutineCompletionEntity>
}

@Dao
interface MedicationDao {
    @Query("SELECT * FROM medication WHERE active = 1 ORDER BY hour, minute")
    fun observeActive(): Flow<List<MedicationEntity>>

    @Query("SELECT * FROM medication")
    suspend fun getAll(): List<MedicationEntity>

    @Query("SELECT * FROM medication WHERE id = :id LIMIT 1")
    suspend fun get(id: Long): MedicationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: MedicationEntity): Long

    @Query("UPDATE medication SET active = 0 WHERE id = :id")
    suspend fun deactivate(id: Long)

    @Query("SELECT * FROM medication_log WHERE date = :date")
    fun observeLogForDate(date: String): Flow<List<MedicationLogEntity>>

    @Query("SELECT * FROM medication_log")
    suspend fun getAllLogs(): List<MedicationLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLog(entity: MedicationLogEntity)

    @Query("DELETE FROM medication_log WHERE `key` = :key")
    suspend fun deleteLog(key: String)
}

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminder ORDER BY hour, minute")
    fun observeAll(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminder")
    suspend fun getAll(): List<ReminderEntity>

    @Query("SELECT * FROM reminder WHERE enabled = 1")
    suspend fun getEnabled(): List<ReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ReminderEntity): Long

    @Query("DELETE FROM reminder WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface DoctorVisitDao {
    @Query("SELECT * FROM doctor_visit ORDER BY dateTimeEpochMillis ASC")
    fun observeAll(): Flow<List<DoctorVisitEntity>>

    @Query("SELECT * FROM doctor_visit")
    suspend fun getAll(): List<DoctorVisitEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: DoctorVisitEntity): Long

    @Query("UPDATE doctor_visit SET done = :done WHERE id = :id")
    suspend fun setDone(id: Long, done: Boolean)

    @Query("DELETE FROM doctor_visit WHERE id = :id")
    suspend fun delete(id: Long)
}
