package com.garbhyatra.app.feature.me

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.garbhyatra.app.di.AppContainer
import com.garbhyatra.app.ui.AppViewModelProvider
import com.garbhyatra.app.ui.components.DisclaimerBanner
import com.garbhyatra.app.ui.components.SectionHeader
import com.garbhyatra.app.ui.components.SoftCard

@Composable
fun MeScreen(
    container: AppContainer,
    onOpenMedications: () -> Unit = {},
    onOpenReminders: () -> Unit = {},
    onOpenDoctorVisits: () -> Unit = {},
    viewModel: TrackersViewModel = viewModel(factory = AppViewModelProvider.factory(container))
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var journalText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(stringResource(R.string.me_title), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        // Care quick links: medications, reminders, doctor visits
        SoftCard(
            modifier = Modifier.clickable { onOpenMedications() },
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
        ) {
            Text("💊  " + stringResource(R.string.care_meds_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(stringResource(R.string.care_meds_subtitle), style = MaterialTheme.typography.bodySmall)
        }
        SoftCard(
            modifier = Modifier.clickable { onOpenReminders() },
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ) {
            Text("⏰  " + stringResource(R.string.care_reminders_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(stringResource(R.string.care_reminders_subtitle), style = MaterialTheme.typography.bodySmall)
        }
        SoftCard(
            modifier = Modifier.clickable { onOpenDoctorVisits() },
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        ) {
            Text("🩺  " + stringResource(R.string.care_visits_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(stringResource(R.string.care_visits_subtitle), style = MaterialTheme.typography.bodySmall)
        }

        // Water
        SoftCard {
            SectionHeader(stringResource(R.string.water_title))
            Text(stringResource(R.string.water_progress, state.waterGlasses))
            Button(onClick = { viewModel.addGlass() }) {
                Text("+ " + stringResource(R.string.water_add))
            }
            Text(stringResource(R.string.water_disclaimer), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Mood
        SoftCard {
            SectionHeader(stringResource(R.string.mood_title))
            Text(stringResource(R.string.mood_prompt))
            val moods = listOf(
                "happy" to R.string.mood_happy,
                "calm" to R.string.mood_calm,
                "tired" to R.string.mood_tired,
                "anxious" to R.string.mood_anxious,
                "sad" to R.string.mood_sad,
                "energetic" to R.string.mood_energetic
            )
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                moods.forEach { (key, labelRes) ->
                    AssistChip(
                        onClick = { viewModel.setMood(key) },
                        label = { Text(stringResource(labelRes)) },
                        colors = if (state.mood == key)
                            AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        else AssistChipDefaults.assistChipColors()
                    )
                }
            }
            Text(stringResource(R.string.mood_support), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Journal
        SoftCard {
            SectionHeader(stringResource(R.string.journal_title))
            Text(stringResource(R.string.journal_prompt))
            OutlinedTextField(
                value = journalText,
                onValueChange = { journalText = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )
            Button(onClick = {
                viewModel.addJournal(journalText)
                journalText = ""
            }) { Text(stringResource(R.string.common_continue)) }

            state.journal.take(5).forEach { entry ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("• ${entry.body}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        DisclaimerBanner(modifier = Modifier.fillMaxWidth())
    }
}
