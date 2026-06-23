package com.garbhyatra.app.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.garbhyatra.app.R
import com.garbhyatra.app.di.AppContainer
import com.garbhyatra.app.domain.model.Stage
import com.garbhyatra.app.ui.AppViewModelProvider
import com.garbhyatra.app.ui.components.DisclaimerBanner
import com.garbhyatra.app.ui.components.SoftCard

private enum class Step { INTRO, NAME, STAGE, CONSENT }

@Composable
fun OnboardingFlow(
    container: AppContainer,
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = viewModel(factory = AppViewModelProvider.factory(container))
) {
    var step by remember { mutableStateOf(Step.INTRO) }
    var name by remember { mutableStateOf("") }
    var stage by remember { mutableStateOf(Stage.T1) }

    when (step) {
        Step.INTRO -> IntroStep(onNext = { step = Step.NAME })
        Step.NAME -> NameStep(
            name = name,
            onNameChange = { name = it },
            onNext = { step = Step.STAGE },
            onSkip = { step = Step.STAGE }
        )
        Step.STAGE -> StageStep(
            selected = stage,
            onSelect = { stage = it },
            onNext = { step = Step.CONSENT }
        )
        Step.CONSENT -> ConsentStep(
            onAccept = { viewModel.finish(name, stage, onFinished) }
        )
    }
}

@Composable
private fun OnboardingScaffold(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) { content() }
}

@Composable
private fun IntroStep(onNext: () -> Unit) {
    OnboardingScaffold {
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.app_tagline),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        IntroBullet(R.string.onb_intro1_title, R.string.onb_intro1_sub)
        IntroBullet(R.string.onb_intro2_title, R.string.onb_intro2_sub)
        IntroBullet(R.string.onb_intro3_title, R.string.onb_intro3_sub)
        Spacer(Modifier.height(8.dp))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.common_start))
        }
    }
}

@Composable
private fun IntroBullet(titleRes: Int, subRes: Int) {
    SoftCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
        Text(stringResource(titleRes), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(stringResource(subRes), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun NameStep(
    name: String,
    onNameChange: (String) -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    OnboardingScaffold {
        Spacer(Modifier.height(24.dp))
        Text(stringResource(R.string.onb_name_title), style = MaterialTheme.typography.headlineMedium)
        Text(stringResource(R.string.onb_name_helper), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.onb_name_hint)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.common_next))
        }
        OutlinedButton(onClick = onSkip, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.common_skip))
        }
    }
}

@Composable
private fun StageStep(
    selected: Stage,
    onSelect: (Stage) -> Unit,
    onNext: () -> Unit
) {
    val options = listOf(
        Triple(Stage.PLANNING, R.string.stage_planning, R.string.stage_planning_sub),
        Triple(Stage.T1, R.string.stage_t1, R.string.stage_t1_sub),
        Triple(Stage.T2, R.string.stage_t2, R.string.stage_t2_sub),
        Triple(Stage.T3, R.string.stage_t3, R.string.stage_t3_sub)
    )
    OnboardingScaffold {
        Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.onb_stage_title), style = MaterialTheme.typography.headlineMedium)
        Text(stringResource(R.string.onb_stage_helper), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        options.forEach { (s, titleRes, subRes) ->
            val isSelected = s == selected
            SoftCard(
                modifier = Modifier.clickableCard { onSelect(s) },
                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface
            ) {
                Text(stringResource(titleRes), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(stringResource(subRes), style = MaterialTheme.typography.bodyMedium)
            }
        }
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.common_next))
        }
    }
}

@Composable
private fun ConsentStep(onAccept: () -> Unit) {
    var c1 by remember { mutableStateOf(false) }
    var c2 by remember { mutableStateOf(false) }
    OnboardingScaffold {
        Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.onb_consent_title), style = MaterialTheme.typography.headlineMedium)
        DisclaimerBanner(text = stringResource(R.string.onb_consent_body))
        CheckRow(checked = c1, onChecked = { c1 = it }, label = stringResource(R.string.onb_consent_check1))
        CheckRow(checked = c2, onChecked = { c2 = it }, label = stringResource(R.string.onb_consent_check2))
        Button(
            onClick = onAccept,
            enabled = c1 && c2,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.onb_consent_cta))
        }
    }
}

@Composable
private fun CheckRow(checked: Boolean, onChecked: (Boolean) -> Unit, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Checkbox(checked = checked, onCheckedChange = onChecked)
        Text(label, style = MaterialTheme.typography.bodyLarge)
    }
}

private fun Modifier.clickableCard(onClick: () -> Unit): Modifier =
    this.clickable(onClick = onClick)
