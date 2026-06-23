package com.garbhyatra.app.feature.today

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.garbhyatra.app.R
import com.garbhyatra.app.data.content.ContentItem
import com.garbhyatra.app.data.content.RoutineTask
import com.garbhyatra.app.di.AppContainer
import com.garbhyatra.app.ui.AppViewModelProvider
import com.garbhyatra.app.ui.components.DisclaimerBanner
import com.garbhyatra.app.ui.components.SectionHeader
import com.garbhyatra.app.ui.components.SoftCard
import com.garbhyatra.app.ui.components.YouTubePlayerDialog
import com.garbhyatra.app.ui.components.rememberGarbhTts
import java.time.LocalTime

@Composable
fun TodayScreen(
    container: AppContainer,
    onOpenStory: (String) -> Unit = {},
    viewModel: TodayViewModel = viewModel(factory = AppViewModelProvider.factory(container))
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var nowPlaying by remember { mutableStateOf<ContentItem?>(null) }
    val tts = rememberGarbhTts()

    nowPlaying?.let { item ->
        YouTubePlayerDialog(
            titleMr = item.titleMr,
            youtubeId = item.youtubeId,
            query = item.youtubeQuery,
            onDismiss = { nowPlaying = null }
        )
    }

    if (state.loading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) { CircularProgressIndicator() }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        GreetingHeader(name = state.name, dayNumber = state.dayNumber)

        val plan = state.plan
        if (plan == null) {
            Text(stringResource(R.string.disclaimer_short))
            return@Column
        }

        plan.slots.affirmation?.bodyMr?.let { affirmation ->
            SoftCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                SectionHeader("✨ " + stringResource(R.string.today_affirmation_header))
                Text(affirmation, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Medium)
            }
        }

        plan.slots.garbhSamvad?.let { samvad ->
            SoftCard(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionHeader("💬 " + stringResource(R.string.today_samvad_header), modifier = Modifier.weight(1f))
                    val speaking = tts.speakingKey == samvad.id
                    IconButton(
                        onClick = {
                            val text = listOfNotNull(samvad.titleMr, samvad.bodyMr).joinToString(". ")
                            tts.toggle(samvad.id, text)
                        }
                    ) {
                        Icon(
                            imageVector = if (speaking) Icons.Filled.Stop else Icons.Filled.VolumeUp,
                            contentDescription = stringResource(
                                if (speaking) R.string.tts_stop else R.string.tts_listen
                            ),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                samvad.titleMr?.let { Text(it, fontWeight = FontWeight.SemiBold) }
                samvad.bodyMr?.let { Text(it, style = MaterialTheme.typography.bodyLarge) }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            plan.slots.meditation?.let { med ->
                SoftCard(modifier = Modifier.weight(1f)) {
                    Text("🌬 " + stringResource(R.string.today_breathing), fontWeight = FontWeight.SemiBold)
                    med.audioDurationSec?.let { Text("${it / 60} मिनिटे", style = MaterialTheme.typography.bodySmall) }
                }
            }
            plan.slots.audio?.let { audio ->
                SoftCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { nowPlaying = audio }
                ) {
                    Text("🎵 " + stringResource(R.string.today_music), fontWeight = FontWeight.SemiBold)
                    Text("▶ " + stringResource(R.string.audio_listen), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        plan.slots.mantra?.let { mantra ->
            SoftCard(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.clickable { nowPlaying = mantra }
            ) {
                SectionHeader("🪔 " + stringResource(R.string.today_mantra_header))
                mantra.titleMr?.let { Text(it, fontWeight = FontWeight.SemiBold) }
                Text("▶ " + stringResource(R.string.audio_listen), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
        }

        plan.slots.story?.let { story ->
            SoftCard(
                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f),
                modifier = Modifier.clickable { onOpenStory(story.storyId) }
            ) {
                SectionHeader("📖 " + stringResource(R.string.today_story_header))
                story.titleMr?.let { Text(it, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium) }
            }
        }

        if (plan.routine.isNotEmpty()) {
            SoftCard {
                val done = state.completedTasks
                SectionHeader(
                    "✅ " + stringResource(R.string.today_routine_header) +
                        "   ${done.count { key -> plan.routine.any { it.taskKey == key } }}/${plan.routine.size}"
                )
                plan.routine.forEach { task ->
                    RoutineRow(
                        task = task,
                        checked = done.contains(task.taskKey),
                        onToggle = { viewModel.toggleTask(task.taskKey, it) }
                    )
                }
            }
        }

        SoftCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("💧 " + stringResource(R.string.today_quick_water) + ": ${state.waterGlasses}")
                Text(
                    "+ " + stringResource(R.string.water_add),
                    modifier = Modifier.clickable { viewModel.addGlass() },
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        plan.slots.tip?.bodyMr?.let { tip ->
            SoftCard(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                SectionHeader("🍃 " + stringResource(R.string.today_tip_header))
                Text(tip, style = MaterialTheme.typography.bodyLarge)
            }
        }

        DisclaimerBanner(modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun GreetingHeader(name: String, dayNumber: Int) {
    val hour = LocalTime.now().hour
    val greetingRes = when {
        hour < 12 -> R.string.today_greeting_morning
        hour < 17 -> R.string.today_greeting_afternoon
        else -> R.string.today_greeting_evening
    }
    val greeting = stringResource(greetingRes)
    Column {
        Text(
            text = if (name.isBlank()) "$greeting 🌸" else "$greeting, $name 🌸",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.day_chip, dayNumber),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RoutineRow(task: RoutineTask, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = onToggle)
        Text(task.titleMr, style = MaterialTheme.typography.bodyLarge)
    }
}
