package com.garbhyatra.app.feature.calm

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.garbhyatra.app.data.content.AudioTrack
import com.garbhyatra.app.di.AppContainer
import com.garbhyatra.app.ui.components.SectionHeader
import com.garbhyatra.app.ui.components.SoftCard
import com.garbhyatra.app.ui.components.YouTubePlayerDialog

private data class CalmCategory(val code: String, val emoji: String, val titleRes: Int)

private val CALM_CATEGORIES = listOf(
    CalmCategory("music", "🎵", R.string.calm_cat_music),
    CalmCategory("mantra", "🪔", R.string.calm_cat_mantra),
    CalmCategory("prarthana", "🙏", R.string.calm_cat_prarthana),
    CalmCategory("shlok", "📜", R.string.calm_cat_shlok),
    CalmCategory("dhyan", "🌬", R.string.calm_cat_dhyan)
)

@Composable
fun CalmScreen(container: AppContainer) {
    var catalog by remember { mutableStateOf<List<AudioTrack>>(emptyList()) }
    var nowPlaying by remember { mutableStateOf<AudioTrack?>(null) }
    LaunchedEffect(Unit) { catalog = container.contentRepository.audioCatalog() }

    nowPlaying?.let { track ->
        YouTubePlayerDialog(
            titleMr = track.titleMr,
            youtubeId = track.youtubeId.ifBlank { null },
            query = track.youtubeQuery,
            onDismiss = { nowPlaying = null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(R.string.calm_title), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(stringResource(R.string.calm_subtitle), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(stringResource(R.string.audio_source_note), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

        CALM_CATEGORIES.forEach { category ->
            val tracks = catalog.filter { it.category == category.code }
            if (tracks.isNotEmpty()) {
                SectionHeader("${category.emoji} " + stringResource(category.titleRes))
                tracks.forEach { track ->
                    SoftCard(
                        modifier = Modifier.clickable { nowPlaying = track }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(track.titleMr, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                            Text(
                                "▶ " + stringResource(R.string.audio_listen),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
