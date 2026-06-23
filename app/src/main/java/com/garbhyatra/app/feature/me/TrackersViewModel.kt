package com.garbhyatra.app.feature.me

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garbhyatra.app.data.local.JournalEntity
import com.garbhyatra.app.data.local.TrackerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TrackersUiState(
    val waterGlasses: Int = 0,
    val mood: String? = null,
    val journal: List<JournalEntity> = emptyList()
)

class TrackersViewModel(
    private val repository: TrackerRepository
) : ViewModel() {

    val uiState: StateFlow<TrackersUiState> = combine(
        repository.observeWaterToday(),
        repository.observeMoodToday(),
        repository.observeJournal()
    ) { water, mood, journal ->
        TrackersUiState(
            waterGlasses = water?.glasses ?: 0,
            mood = mood?.mood,
            journal = journal
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TrackersUiState()
    )

    fun addGlass() {
        viewModelScope.launch { repository.addGlass() }
    }

    fun setMood(mood: String) {
        viewModelScope.launch { repository.setMood(mood) }
    }

    fun addJournal(text: String) {
        viewModelScope.launch { repository.addJournal(text) }
    }
}
