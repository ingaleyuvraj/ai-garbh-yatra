package com.garbhyatra.app.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.garbhyatra.app.domain.model.Stage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "garbhyatra_prefs")

/** User profile + onboarding state persisted in DataStore. */
data class UserPrefs(
    val onboarded: Boolean = false,
    val name: String = "",
    val stage: Stage = Stage.T1,
    val installEpochDay: Long = LocalDate.now().toEpochDay(),
    val consentAccepted: Boolean = false
)

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val ONBOARDED = booleanPreferencesKey("onboarded")
        val NAME = stringPreferencesKey("name")
        val STAGE = stringPreferencesKey("stage")
        val INSTALL_EPOCH_DAY = longPreferencesKey("install_epoch_day")
        val CONSENT = booleanPreferencesKey("consent")
    }

    val userPrefs: Flow<UserPrefs> = context.dataStore.data.map { p ->
        UserPrefs(
            onboarded = p[Keys.ONBOARDED] ?: false,
            name = p[Keys.NAME] ?: "",
            stage = Stage.fromCode(p[Keys.STAGE]),
            installEpochDay = p[Keys.INSTALL_EPOCH_DAY] ?: LocalDate.now().toEpochDay(),
            consentAccepted = p[Keys.CONSENT] ?: false
        )
    }

    suspend fun saveName(name: String) {
        context.dataStore.edit { it[Keys.NAME] = name }
    }

    suspend fun saveStage(stage: Stage) {
        context.dataStore.edit { it[Keys.STAGE] = stage.code }
    }

    suspend fun completeOnboarding(name: String, stage: Stage) {
        context.dataStore.edit { p ->
            p[Keys.NAME] = name
            p[Keys.STAGE] = stage.code
            p[Keys.CONSENT] = true
            p[Keys.ONBOARDED] = true
            if (p[Keys.INSTALL_EPOCH_DAY] == null) {
                p[Keys.INSTALL_EPOCH_DAY] = LocalDate.now().toEpochDay()
            }
        }
    }

    /** Restores preferences from a backup snapshot. */
    suspend fun restore(
        name: String,
        stageCode: String,
        installEpochDay: Long,
        onboarded: Boolean,
        consent: Boolean
    ) {
        context.dataStore.edit { p ->
            p[Keys.NAME] = name
            p[Keys.STAGE] = stageCode
            p[Keys.INSTALL_EPOCH_DAY] = installEpochDay
            p[Keys.ONBOARDED] = onboarded
            p[Keys.CONSENT] = consent
        }
    }
}
