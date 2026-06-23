package com.garbhyatra.app.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.initializer
import com.garbhyatra.app.di.AppContainer
import com.garbhyatra.app.feature.care.CareViewModel
import com.garbhyatra.app.feature.me.TrackersViewModel
import com.garbhyatra.app.feature.onboarding.OnboardingViewModel
import com.garbhyatra.app.feature.today.TodayViewModel

/** Centralised ViewModel factories wired to the manual [AppContainer]. */
object AppViewModelProvider {

    fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
        initializer { OnboardingViewModel(container.userPreferencesRepository) }
        initializer {
            TodayViewModel(
                container.userPreferencesRepository,
                container.routineEngine,
                container.contentRepository,
                container.trackerRepository
            )
        }
        initializer { TrackersViewModel(container.trackerRepository) }
        initializer { CareViewModel(container.careRepository) }
    }
}
