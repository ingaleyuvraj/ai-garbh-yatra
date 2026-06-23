package com.garbhyatra.app.data.content

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * Loads the bundled Marathi content from assets and caches it in memory.
 * Offline-first: content ships inside the app under assets/content/.
 */
class ContentRepository(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Volatile
    private var cached: ContentBundle? = null

    suspend fun loadBundle(): ContentBundle {
        cached?.let { return it }
        return withContext(Dispatchers.IO) {
            val text = context.assets.open(ASSET_PATH).bufferedReader().use { it.readText() }
            val bundle = json.decodeFromString<ContentBundle>(text)
            cached = bundle
            bundle
        }
    }

    /** Returns all day plans for a stage, ordered by dayIndex. */
    suspend fun daysForStage(stageCode: String): List<DayPlan> {
        val days = loadBundle().days.filter { it.stage == stageCode }
            .sortedBy { it.dayIndex }
        // Fallback to t1 program if a stage has no dedicated content yet.
        return days.ifEmpty { loadBundle().days.filter { it.stage == "t1" }.sortedBy { it.dayIndex } }
    }

    /** All audio tracks (YouTube-backed) in the bundle. */
    suspend fun audioCatalog(): List<AudioTrack> = loadBundle().audioCatalog

    /** Audio tracks filtered by category (music/mantra/prarthana/shlok/dhyan). */
    suspend fun audioByCategory(category: String): List<AudioTrack> =
        loadBundle().audioCatalog.filter { it.category == category }

    /** All story series in the bundle. */
    suspend fun stories(): List<Story> = loadBundle().stories

    /** A single story series by id. */
    suspend fun storyById(id: String): Story? = loadBundle().stories.firstOrNull { it.id == id }

    companion object {
        private const val ASSET_PATH = "content/journey.json"
    }
}
