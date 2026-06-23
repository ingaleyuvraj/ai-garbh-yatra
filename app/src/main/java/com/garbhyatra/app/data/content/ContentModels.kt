package com.garbhyatra.app.data.content

import kotlinx.serialization.Serializable

/** Serializable models matching content/journey.json (see content/schema.json). */

@Serializable
data class ContentBundle(
    val version: String = "1.0.0",
    val lang: String = "mr",
    val disclaimer: String = "",
    val audioCatalog: List<AudioTrack> = emptyList(),
    val stories: List<Story> = emptyList(),
    val days: List<DayPlan> = emptyList()
)

@Serializable
data class DayPlan(
    val id: String,
    val stage: String,
    val dayIndex: Int,
    val titleMr: String? = null,
    val disclaimerMr: String,
    val slots: Slots,
    val routine: List<RoutineTask> = emptyList()
)

@Serializable
data class Slots(
    val affirmation: ContentItem? = null,
    val garbhSamvad: ContentItem? = null,
    val meditation: ContentItem? = null,
    val mantra: ContentItem? = null,
    val audio: ContentItem? = null,
    val tip: ContentItem? = null,
    val partnerTask: ContentItem? = null,
    val story: StoryRef? = null
)

@Serializable
data class ContentItem(
    val id: String,
    val type: String,
    val stage: String = "t1",
    val lang: String = "mr",
    val titleMr: String? = null,
    val bodyMr: String? = null,
    val meaningMr: String? = null,
    val audioUrl: String? = null,
    val audioDurationSec: Int? = null,
    val imageUrl: String? = null,
    val youtubeId: String? = null,
    val youtubeQuery: String? = null,
    val license: String? = null,
    val attribution: String? = null,
    val isPremium: Boolean = false,
    val tags: List<String> = emptyList()
)

@Serializable
data class RoutineTask(
    val taskKey: String,
    val titleMr: String,
    val target: Int? = null,
    val deepLink: String? = null
)

/** Reference from a day plan to a specific section of a [Story]. */
@Serializable
data class StoryRef(
    val storyId: String,
    val sectionIndex: Int = 0,
    val titleMr: String? = null
)

@Serializable
data class AudioTrack(
    val id: String,
    val category: String,
    val titleMr: String,
    val youtubeId: String = "",
    val youtubeQuery: String = "",
    val license: String? = null,
    val attribution: String? = null
)

@Serializable
data class Story(
    val id: String,
    val category: String,
    val titleMr: String,
    val summaryMr: String = "",
    val sections: List<StorySection> = emptyList()
)

@Serializable
data class StorySection(
    val titleMr: String,
    val bodyMr: String,
    val moralMr: String? = null
)
