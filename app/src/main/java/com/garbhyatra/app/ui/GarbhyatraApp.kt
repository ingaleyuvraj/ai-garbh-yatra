package com.garbhyatra.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.garbhyatra.app.data.prefs.UserPrefs
import com.garbhyatra.app.di.AppContainer
import com.garbhyatra.app.feature.onboarding.OnboardingFlow
import com.garbhyatra.app.ui.navigation.MainScaffold
import com.garbhyatra.app.ui.navigation.Routes

@Composable
fun GarbhyatraApp(container: AppContainer) {
    val prefs: UserPrefs by container.userPreferencesRepository.userPrefs
        .collectAsStateWithLifecycle(initialValue = UserPrefs())

    val rootNavController = rememberNavController()
    val start = if (prefs.onboarded) Routes.MAIN else Routes.ONBOARDING

    Scaffold { innerPadding ->
        NavHost(
            navController = rootNavController,
            startDestination = start,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.ONBOARDING) {
                OnboardingFlow(
                    container = container,
                    onFinished = {
                        rootNavController.navigate(Routes.MAIN) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.MAIN) {
                MainScaffold(container = container, userName = prefs.name)
            }
        }
    }
}
