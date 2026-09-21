package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.VerticalSplit
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewStream
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun DualRunnerScreen(
    cloneA: CloneInstance?,
    cloneB: CloneInstance?,
    allClones: List<CloneInstance>,
    isVerticalSplit: Boolean,
    onSelectPaneA: (CloneInstance) -> Unit,
    onSelectPaneB: (CloneInstance) -> Unit,
    onSwapPanes: () -> Unit,
    onToggleOrientation: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenuA by remember { mutableStateOf(false) }
    var showMenuB by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("dual_runner_screen")
    ) {
        // Dual Mode Toolbar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onClose, modifier = Modifier.size(36.dp)) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Selector for Pane A
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                            .clickable { showMenuA = true }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val accentA = if (cloneA != null) InstalledAppHelper.parseColor(cloneA.badgeColorHex) else MaterialTheme.colorScheme.primary
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(accentA)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = cloneA?.instanceName ?: "Select App 1",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                    }

                    DropdownMenu(
                        expanded = showMenuA,
                        onDismissRequest = { showMenuA = false }
                    ) {
                        allClones.forEach { clone ->
                            DropdownMenuItem(
                                text = { Text(clone.instanceName) },
                                onClick = {
                                    onSelectPaneA(clone)
                                    showMenuA = false
                                },
                                leadingIcon = {
                                    val col = InstalledAppHelper.parseColor(clone.badgeColorHex)
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(col)
                                    )
                                },
                                trailingIcon = if (clone.id == cloneA?.id) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null
                            )
                        }
                    }
                }

                // Swap Button
                IconButton(
                    onClick = onSwapPanes,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.SwapHoriz,
                        contentDescription = "Swap Instances",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Selector for Pane B
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                            .clickable { showMenuB = true }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val accentB = if (cloneB != null) InstalledAppHelper.parseColor(cloneB.badgeColorHex) else MaterialTheme.colorScheme.secondary
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(accentB)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = cloneB?.instanceName ?: "Select App 2",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                    }

                    DropdownMenu(
                        expanded = showMenuB,
                        onDismissRequest = { showMenuB = false }
                    ) {
                        allClones.forEach { clone ->
                            DropdownMenuItem(
                                text = { Text(clone.instanceName) },
                                onClick = {
                                    onSelectPaneB(clone)
                                    showMenuB = false
                                },
                                leadingIcon = {
                                    val col = InstalledAppHelper.parseColor(clone.badgeColorHex)
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(col)
                                    )
                                },
                                trailingIcon = if (clone.id == cloneB?.id) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null
                            )
                        }
                    }
                }

                // Toggle Orientation Button
                IconButton(
                    onClick = onToggleOrientation,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isVerticalSplit) Icons.Default.VerticalSplit else Icons.Default.ViewStream,
                        contentDescription = "Toggle Orientation",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Parallel Viewport Area
        if (isVerticalSplit) {
            // Stacked Top / Bottom Split
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (cloneA != null) {
                        SandboxWebView(
                            clone = cloneA,
                            modifier = Modifier.fillMaxSize(),
                            showControls = true
                        )
                    } else {
                        EmptyPanePlaceholder("Select an instance for Pane A")
                    }
                }

                // Split Divider Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (cloneB != null) {
                        SandboxWebView(
                            clone = cloneB,
                            modifier = Modifier.fillMaxSize(),
                            showControls = true
                        )
                    } else {
                        EmptyPanePlaceholder("Select an instance for Pane B")
                    }
                }
            }
        } else {
            // Side by Side Left / Right Split
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    if (cloneA != null) {
                        SandboxWebView(
                            clone = cloneA,
                            modifier = Modifier.fillMaxSize(),
                            showControls = true
                        )
                    } else {
                        EmptyPanePlaceholder("Select instance 1")
                    }
                }

                // Vertical Divider Bar
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(6.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    if (cloneB != null) {
                        SandboxWebView(
                            clone = cloneB,
                            modifier = Modifier.fillMaxSize(),
                            showControls = true
                        )
                    } else {
                        EmptyPanePlaceholder("Select instance 2")
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyPanePlaceholder(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.VerticalSplit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
