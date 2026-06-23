package com.garbhyatra.app.feature.stories

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.garbhyatra.app.R
import com.garbhyatra.app.data.content.Story
import com.garbhyatra.app.di.AppContainer
import com.garbhyatra.app.ui.components.SectionHeader
import com.garbhyatra.app.ui.components.SoftCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoriesScreen(
    container: AppContainer,
    onBack: () -> Unit,
    onOpenStory: (String) -> Unit
) {
    var stories by remember { mutableStateOf<List<Story>>(emptyList()) }
    LaunchedEffect(Unit) { stories = container.contentRepository.stories() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.stories_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                stringResource(R.string.stories_intro),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            stories.forEach { story ->
                SoftCard(
                    modifier = Modifier.clickable { onOpenStory(story.id) },
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                ) {
                    SectionHeader("📖 " + story.titleMr)
                    if (story.summaryMr.isNotBlank()) {
                        Text(story.summaryMr, style = MaterialTheme.typography.bodyMedium)
                    }
                    Text(
                        stringResource(R.string.stories_sections, story.sections.size),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
