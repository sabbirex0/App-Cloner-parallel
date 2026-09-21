package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.util.AppTemplate
import com.example.util.InstalledAppHelper

val COLOR_OPTIONS = listOf(
    "#0284C7", // Cyan Blue
    "#7C3AED", // Violet
    "#059669", // Emerald
    "#F59E0B", // Amber
    "#EC4899", // Rose Pink
    "#EF4444", // Red
    "#14B8A6", // Teal
    "#334155"  // Slate Dark
)

val BADGE_SUGGESTIONS = listOf("2", "3", "ALT", "WORK", "VIP", "BIZ", "DEV")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateCloneSheet(
    sheetState: SheetState,
    initialTemplate: AppTemplate? = null,
    onPickInstalledApp: () -> Unit,
    onSaveClone: (CloneInstance) -> Unit,
    onDismiss: () -> Unit
) {
    val defaultTemplate = initialTemplate ?: InstalledAppHelper.POPULAR_TEMPLATES.first()

    var selectedAppName by remember { mutableStateOf(defaultTemplate.name) }
    var selectedPackageName by remember { mutableStateOf(defaultTemplate.packageName) }
    var instanceName by remember { mutableStateOf("${defaultTemplate.name} (Instance 2)") }
    var targetUrl by remember { mutableStateOf(defaultTemplate.defaultUrl) }
    var selectedColor by remember { mutableStateOf(defaultTemplate.defaultBadgeColor) }
    var selectedBadge by remember { mutableStateOf(defaultTemplate.defaultBadge) }
    var isPinProtected by remember { mutableStateOf(false) }
    var pinCode by remember { mutableStateOf("1234") }
    var isIncognito by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    var cloneType by remember { mutableStateOf("SANDBOX_WEB") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        modifier = Modifier.testTag("create_clone_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Clone New Application",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Create an isolated, parallel instance",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick App Selection Carousel or Installed App picker
            Text(
                text = "Select Application to Clone",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                InstalledAppHelper.POPULAR_TEMPLATES.take(6).forEach { template ->
                    val isSelected = selectedPackageName == template.packageName && selectedAppName == template.name
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedAppName = template.name
                            selectedPackageName = template.packageName
                            targetUrl = template.defaultUrl
                            selectedColor = template.defaultBadgeColor
                            selectedBadge = template.defaultBadge
                            instanceName = "${template.name} (Instance 2)"
                        },
                        label = { Text(template.name) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }

                // Pick from installed device apps button
                Button(
                    onClick = onPickInstalledApp,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Device Apps...", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Instance Name
            OutlinedTextField(
                value = instanceName,
                onValueChange = { instanceName = it },
                label = { Text("Instance Display Name") },
                placeholder = { Text("e.g. WhatsApp #2, Work Chat") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("instance_name_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Badge Text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = selectedBadge,
                    onValueChange = { if (it.length <= 4) selectedBadge = it.uppercase() },
                    label = { Text("Badge Label") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                // Quick badge suggestion chips
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    BADGE_SUGGESTIONS.take(4).forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (selectedBadge == tag) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { selectedBadge = tag }
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedBadge == tag) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Color Palette Picker
            Text(
                text = "Instance Theme & Badge Color",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                COLOR_OPTIONS.forEach { hex ->
                    val color = InstalledAppHelper.parseColor(hex)
                    val isSelected = selectedColor.equals(hex, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color)
                            .clickable { selectedColor = hex }
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Security & Privacy Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("PIN Lock Protection", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Require 4-digit PIN to open", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = isPinProtected,
                            onCheckedChange = { isPinProtected = it }
                        )
                    }

                    if (isPinProtected) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = pinCode,
                            onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) pinCode = it },
                            label = { Text("Set 4-digit PIN") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Security,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Incognito Mode", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Don't save session cookies on exit", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = isIncognito,
                            onCheckedChange = { isIncognito = it }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notes / Account Identifier
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Account Notes / Tag (Optional)") },
                placeholder = { Text("e.g. +1 555-0199 or work login") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Create Button
            Button(
                onClick = {
                    val newClone = CloneInstance(
                        packageName = selectedPackageName,
                        appName = selectedAppName,
                        instanceName = instanceName.ifBlank { "$selectedAppName #2" },
                        instanceNumber = 2,
                        badgeColorHex = selectedColor,
                        badgeLabel = selectedBadge.ifBlank { "2" },
                        cloneType = cloneType,
                        targetUrl = targetUrl,
                        storageUsageKb = 1024L,
                        isIncognito = isIncognito,
                        isPinProtected = isPinProtected,
                        pinCode = if (isPinProtected) pinCode.ifBlank { "1234" } else "",
                        notes = notes
                    )
                    onSaveClone(newClone)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_create_clone_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Cloned Instance", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
