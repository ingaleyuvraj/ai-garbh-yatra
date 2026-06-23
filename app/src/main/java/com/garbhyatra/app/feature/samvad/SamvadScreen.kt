package com.garbhyatra.app.feature.samvad

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.garbhyatra.app.R
import com.garbhyatra.app.data.content.DayPlan
import com.garbhyatra.app.di.AppContainer
import com.garbhyatra.app.ui.components.SectionHeader
import com.garbhyatra.app.ui.components.SoftCard
import com.garbhyatra.app.ui.components.rememberGarbhTts

@Composable
fun SamvadScreen(container: AppContainer) {
    var days by remember { mutableStateOf<List<DayPlan>>(emptyList()) }
    val tts = rememberGarbhTts()

    LaunchedEffect(Unit) {
        // Load samvad library for current stage (fallback handled in repository).
        days = container.contentRepository.daysForStage("t1")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(R.string.samvad_title), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(stringResource(R.string.samvad_intro), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)

        days.mapNotNull { it.slots.garbhSamvad }.forEach { item ->
            val speaking = tts.speakingKey == item.id
            SoftCard(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item.titleMr?.let { SectionHeader(it, modifier = Modifier.weight(1f)) }
                    IconButton(
                        onClick = {
                            val text = listOfNotNull(item.titleMr, item.bodyMr).joinToString(". ")
                            tts.toggle(item.id, text)
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
                item.bodyMr?.let { Text(it, style = MaterialTheme.typography.bodyLarge) }
            }
        }
    }
}
