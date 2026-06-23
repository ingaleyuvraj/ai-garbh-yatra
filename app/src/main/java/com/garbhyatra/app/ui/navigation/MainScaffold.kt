package com.garbhyatra.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.garbhyatra.app.di.AppContainer
import com.garbhyatra.app.feature.calm.CalmScreen
import com.garbhyatra.app.feature.care.DoctorVisitsScreen
import com.garbhyatra.app.feature.care.MedicationsScreen
import com.garbhyatra.app.feature.care.RemindersScreen
import com.garbhyatra.app.feature.me.MeScreen
import com.garbhyatra.app.feature.more.MoreScreen
import com.garbhyatra.app.feature.samvad.SamvadScreen
import com.garbhyatra.app.feature.stories.StoriesScreen
import com.garbhyatra.app.feature.stories.StoryDetailScreen
import com.garbhyatra.app.feature.today.TodayScreen

@Composable
fun MainScaffold(container: AppContainer, userName: String) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = backStackEntry?.destination
            NavigationBar {
                TopLevelTab.entries.forEach { tab ->
                    val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = stringResource(tab.labelRes)) },
                        label = { Text(stringResource(tab.labelRes)) },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.TODAY,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.TODAY) {
                TodayScreen(
                    container = container,
                    onOpenStory = { storyId -> navController.navigate(Routes.storyDetail(storyId)) }
                )
            }
            composable(Routes.SAMVAD) { SamvadScreen(container = container) }
            composable(Routes.CALM) { CalmScreen(container = container) }
            composable(Routes.ME) {
                MeScreen(
                    container = container,
                    onOpenMedications = { navController.navigate(Routes.MEDICATIONS) },
                    onOpenReminders = { navController.navigate(Routes.REMINDERS) },
                    onOpenDoctorVisits = { navController.navigate(Routes.DOCTOR_VISITS) }
                )
            }
            composable(Routes.MORE) {
                MoreScreen(
                    container = container,
                    onOpenStories = { navController.navigate(Routes.STORIES) }
                )
            }
            composable(Routes.MEDICATIONS) {
                MedicationsScreen(container = container, onBack = { navController.popBackStack() })
            }
            composable(Routes.REMINDERS) {
                RemindersScreen(container = container, onBack = { navController.popBackStack() })
            }
            composable(Routes.DOCTOR_VISITS) {
                DoctorVisitsScreen(container = container, onBack = { navController.popBackStack() })
            }
            composable(Routes.STORIES) {
                StoriesScreen(
                    container = container,
                    onBack = { navController.popBackStack() },
                    onOpenStory = { storyId -> navController.navigate(Routes.storyDetail(storyId)) }
                )
            }
            composable(
                route = Routes.STORY_DETAIL,
                arguments = listOf(navArgument("storyId") { type = NavType.StringType })
            ) { backStackEntry ->
                StoryDetailScreen(
                    container = container,
                    storyId = backStackEntry.arguments?.getString("storyId").orEmpty(),
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
