package com.garbhyatra.app.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import com.garbhyatra.app.R

object Routes {
    const val ONBOARDING = "onboarding"
    const val MAIN = "main"

    const val TODAY = "today"
    const val SAMVAD = "samvad"
    const val CALM = "calm"
    const val ME = "me"
    const val MORE = "more"

    const val STORIES = "stories"
    const val STORY_DETAIL = "story/{storyId}"
    fun storyDetail(storyId: String) = "story/$storyId"

    const val MEDICATIONS = "medications"
    const val REMINDERS = "reminders"
    const val DOCTOR_VISITS = "doctor_visits"
}

enum class TopLevelTab(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector
) {
    TODAY(Routes.TODAY, R.string.nav_today, Icons.Outlined.WbSunny),
    SAMVAD(Routes.SAMVAD, R.string.nav_samvad, Icons.Outlined.Favorite),
    CALM(Routes.CALM, R.string.nav_calm, Icons.Outlined.SelfImprovement),
    ME(Routes.ME, R.string.nav_me, Icons.Outlined.Person),
    MORE(Routes.MORE, R.string.nav_more, Icons.Outlined.MoreHoriz)
}
