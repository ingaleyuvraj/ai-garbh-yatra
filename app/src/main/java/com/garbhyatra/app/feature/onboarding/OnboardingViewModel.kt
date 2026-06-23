package com.garbhyatra.app.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garbhyatra.app.data.prefs.UserPreferencesRepository
import com.garbhyatra.app.domain.model.Stage
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val prefs: UserPreferencesRepository
) : ViewModel() {

    fun finish(name: String, stage: Stage, onDone: () -> Unit) {
        viewModelScope.launch {
            prefs.completeOnboarding(name.trim(), stage)
            onDone()
        }
    }
}
