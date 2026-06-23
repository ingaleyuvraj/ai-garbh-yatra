package com.garbhyatra.app.domain.routine

import com.garbhyatra.app.data.content.ContentRepository
import com.garbhyatra.app.data.content.DayPlan
import com.garbhyatra.app.domain.model.Stage
import java.time.LocalDate

/**
 * Deterministic daily routine engine (see docs/04-content-strategy.md §2).
 * Given the install date and today's date, it picks a stable day plan from the
 * stage's content pool. Fully offline — runs on the bundled content.
 */
class RoutineEngine(private val contentRepository: ContentRepository) {

    suspend fun todayPlan(
        stage: Stage,
        installEpochDay: Long,
        today: LocalDate = LocalDate.now()
    ): DayPlan? {
        val days = contentRepository.daysForStage(stage.code)
        if (days.isEmpty()) return null
        val elapsed = (today.toEpochDay() - installEpochDay).coerceAtLeast(0)
        val index = (elapsed % days.size).toInt()
        return days[index]
    }

    /** 1-based day number shown in the UI ("दिवस N"), cycling through the program. */
    fun dayNumber(installEpochDay: Long, poolSize: Int, today: LocalDate = LocalDate.now()): Int {
        if (poolSize <= 0) return 1
        val elapsed = (today.toEpochDay() - installEpochDay).coerceAtLeast(0)
        return (elapsed % poolSize).toInt() + 1
    }
}
