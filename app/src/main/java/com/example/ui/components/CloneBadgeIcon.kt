package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CloneInstance
import com.example.util.InstalledAppHelper

@Composable
fun CloneBadgeIcon(
    clone: CloneInstance,
    size: Dp = 56.dp,
    modifier: Modifier = Modifier
) {
    val accentColor = InstalledAppHelper.parseColor(clone.badgeColorHex)
    val iconVector = getAppIconVector(clone.packageName, clone.appName)

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Main App Tile
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(size * 0.26f))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.25f),
                            accentColor.copy(alpha = 0.10f),
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    color = accentColor.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(size * 0.26f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = clone.appName,
                tint = accentColor,
                modifier = Modifier.size(size * 0.54f)
            )
        }

        // Clone Number / Label Badge Overlay
        val badgeSize = (size * 0.44f).coerceAtLeast(18.dp)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (size * 0.08f), y = (size * 0.08f))
                .size(badgeSize)
                .clip(CircleShape)
                .background(accentColor)
                .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = clone.badgeLabel.take(3),
                color = Color.White,
                fontSize = (badgeSize.value * 0.45f).sp,
                fontWeight = FontWeight.Black,
                maxLines = 1
            )
        }

        // Security / PIN lock badge if protected
        if (clone.isPinProtected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = -(size * 0.08f), y = -(size * 0.08f))
                    .size(badgeSize * 0.85f)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiary)
                    .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "PIN Protected",
                    tint = Color.White,
                    modifier = Modifier.size(badgeSize * 0.5f)
                )
            }
        }
    }
}

fun getAppIconVector(packageName: String, appName: String): ImageVector {
    val name = (appName + packageName).lowercase()
    return when {
        name.contains("whatsapp") -> Icons.Default.Chat
        name.contains("telegram") -> Icons.Default.Forum
        name.contains("twitter") || name.contains("x") -> Icons.Default.Language
        name.contains("facebook") || name.contains("messenger") -> Icons.Default.Forum
        name.contains("mail") || name.contains("gmail") -> Icons.Default.Email
        name.contains("youtube") || name.contains("video") -> Icons.Default.PlayArrow
        name.contains("security") || name.contains("vault") -> Icons.Default.Security
        else -> Icons.Default.Apps
    }
}
