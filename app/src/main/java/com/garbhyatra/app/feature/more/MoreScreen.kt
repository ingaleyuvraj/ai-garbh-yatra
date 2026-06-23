package com.garbhyatra.app.feature.more

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.garbhyatra.app.R
import com.garbhyatra.app.di.AppContainer
import com.garbhyatra.app.ui.components.DisclaimerBanner
import com.garbhyatra.app.ui.components.SectionHeader
import com.garbhyatra.app.ui.components.SoftCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MoreScreen(
    container: AppContainer,
    onOpenStories: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val exportMsg = stringResource(R.string.data_export_done)
    val importMsg = stringResource(R.string.data_import_done)
    val errorMsg = stringResource(R.string.data_error)

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val message = try {
                    withContext(Dispatchers.IO) { container.backupRepository.exportTo(uri) }
                    exportMsg
                } catch (e: Exception) {
                    errorMsg
                }
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val message = try {
                    withContext(Dispatchers.IO) { container.backupRepository.importFrom(uri) }
                    importMsg
                } catch (e: Exception) {
                    errorMsg
                }
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(stringResource(R.string.more_title), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

            SoftCard(
                modifier = Modifier.clickable { onOpenStories() },
                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f)
            ) {
                Text("📖  " + stringResource(R.string.more_stories), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(stringResource(R.string.stories_intro), style = MaterialTheme.typography.bodySmall)
            }

            // Data export / import
            SoftCard(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)) {
                SectionHeader(stringResource(R.string.data_title))
                Text(stringResource(R.string.data_subtitle), style = MaterialTheme.typography.bodySmall)
                SoftCard(
                    modifier = Modifier.clickable { exportLauncher.launch("garbhyatra-backup.json") },
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Text("⬆  " + stringResource(R.string.data_export), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.data_export_hint), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                SoftCard(
                    modifier = Modifier.clickable { importLauncher.launch(arrayOf("application/json")) },
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Text("⬇  " + stringResource(R.string.data_import), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.data_import_hint), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            val items = listOf(
                "🍃" to R.string.more_diet,
                "👫" to R.string.more_partner,
                "⬇" to R.string.more_offline,
                "🛡" to R.string.more_safety,
                "❓" to R.string.more_faq,
                "⚙" to R.string.more_settings,
                "🔒" to R.string.more_privacy,
                "⭐" to R.string.more_premium
            )
            items.forEach { (emoji, labelRes) ->
                SoftCard {
                    Text("$emoji  " + stringResource(labelRes), style = MaterialTheme.typography.titleMedium)
                }
            }

            SoftCard(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                SectionHeader(stringResource(R.string.more_safety))
                Text(stringResource(R.string.safety_body), style = MaterialTheme.typography.bodyMedium)
            }
            DisclaimerBanner(modifier = Modifier.fillMaxWidth())
        }
    }
}
