package com.garbhyatra.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.garbhyatra.app.R
import com.garbhyatra.app.ui.theme.DisclaimerColor

/**
 * Persistent doctor-respect disclaimer. Mandatory on every health-adjacent screen.
 */
@Composable
fun DisclaimerBanner(
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.disclaimer_short)
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DisclaimerColor.copy(alpha = 0.12f))
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .semantics { contentDescription = text },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = DisclaimerColor
        )
        Text(
            text = text,
            color = DisclaimerColor,
            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
            overflow = TextOverflow.Visible
        )
    }
}
