package com.garbhyatra.app.feature.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garbhyatra.app.data.content.ContentRepository
import com.garbhyatra.app.data.content.DayPlan
import com.garbhyatra.app.data.local.TrackerRepository
import com.garbhyatra.app.data.prefs.UserPreferencesRepository
import com.garbhyatra.app.domain.routine.RoutineEngine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TodayUiState(
    val loading: Boolean = true,
    val name: String = "",
    val dayNumber: Int = 1,
    val plan: DayPlan? = null,
    val waterGlasses: Int = 0,
    val completedTasks: Set<String> = emptySet()
)

class TodayViewModel(
    private val prefs: UserPreferencesRepository,
    private val routineEngine: RoutineEngine,
    private val contentRepository: ContentRepository,
    private val trackerRepository: TrackerRepository
) : ViewModel() {

    private val planFlow = prefs.userPrefs.map { p ->
        val days = contentRepository.daysForStage(p.stage.code)
        val plan = routineEngine.todayPlan(p.stage, p.installEpochDay)
        val dayNumber = routineEngine.dayNumber(p.installEpochDay, days.size)
        PlanState(p.name, dayNumber, plan)
    }

    val uiState: StateFlow<TodayUiState> = combine(
        planFlow,
        trackerRepository.observeWaterToday(),
        trackerRepository.observeRoutineToday()
    ) { planState, water, completions ->
        TodayUiState(
            loading = false,
            name = planState.name,
            dayNumber = planState.dayNumber,
            plan = planState.plan,
            waterGlasses = water?.glasses ?: 0,
            completedTasks = completions.map { it.taskKey }.toSet()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TodayUiState()
    )

    fun addGlass() {
        viewModelScope.launch { trackerRepository.addGlass() }
    }

    fun toggleTask(taskKey: String, done: Boolean) {
        viewModelScope.launch { trackerRepository.toggleRoutine(taskKey, done) }
    }

    private data class PlanState(val name: String, val dayNumber: Int, val plan: DayPlan?)
}
