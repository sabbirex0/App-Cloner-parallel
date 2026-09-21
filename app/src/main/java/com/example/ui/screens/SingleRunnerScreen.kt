package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.VerticalSplit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CloneInstance
import com.example.runner.SandboxWebView
import com.example.ui.components.CloneBadgeIcon
import com.example.util.InstalledAppHelper

@Composable
fun SingleRunnerScreen(
    clone: CloneInstance,
    onBack: () -> Unit,
    onLaunchDualWithThis: (CloneInstance) -> Unit,
    onLaunchSystemMultiTask: (CloneInstance) -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = InstalledAppHelper.parseColor(clone.badgeColorHex)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("single_runner_screen")
    ) {
        // App Instance Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))
                    CloneBadgeIcon(clone = clone, size = 32.dp)
                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = clone.instanceName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = clone.appName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Split / Dual Runner button
                    IconButton(
                        onClick = { onLaunchDualWithThis(clone) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.VerticalSplit,
                            contentDescription = "Open in Dual Runner",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // System Multitask button
                    IconButton(
                        onClick = { onLaunchSystemMultiTask(clone) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.OpenInNew,
                            contentDescription = "Open in Multitask",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Main Sandbox Web View
        SandboxWebView(
            clone = clone,
            modifier = Modifier.weight(1f),
            showControls = true
        )
    }
}
