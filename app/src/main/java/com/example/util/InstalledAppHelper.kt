package com.example.util

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color

data class AppTemplate(
    val name: String,
    val packageName: String,
    val defaultUrl: String,
    val defaultBadgeColor: String,
    val defaultBadge: String,
    val description: String
)

data class InstalledAppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val isSystemApp: Boolean
)

object InstalledAppHelper {

    val POPULAR_TEMPLATES = listOf(
        AppTemplate(
            name = "WhatsApp",
            packageName = "com.whatsapp",
            defaultUrl = "https://web.whatsapp.com",
            defaultBadgeColor = "#10B981",
            defaultBadge = "2",
            description = "Run a 2nd WhatsApp account with isolated chats and media"
        ),
        AppTemplate(
            name = "Telegram",
            packageName = "org.telegram.messenger",
            defaultUrl = "https://web.telegram.org/a/",
            defaultBadgeColor = "#0284C7",
            defaultBadge = "ALT",
            description = "Separate work channels from private messaging"
        ),
        AppTemplate(
            name = "X (Twitter)",
            packageName = "com.twitter.android",
            defaultUrl = "https://x.com",
            defaultBadgeColor = "#0F172A",
            defaultBadge = "ALT",
            description = "Browse or post from multiple handles at once"
        ),
        AppTemplate(
            name = "Instagram",
            packageName = "com.instagram.android",
            defaultUrl = "https://www.instagram.com",
            defaultBadgeColor = "#EC4899",
            defaultBadge = "BIZ",
            description = "Manage business and personal creator profiles"
        ),
        AppTemplate(
            name = "Discord",
            packageName = "com.discord",
            defaultUrl = "https://discord.com/app",
            defaultBadgeColor = "#6366F1",
            defaultBadge = "GAME",
            description = "Connect multiple gamer and community server tags"
        ),
        AppTemplate(
            name = "Reddit",
            packageName = "com.reddit.frontpage",
            defaultUrl = "https://www.reddit.com",
            defaultBadgeColor = "#F97316",
            defaultBadge = "ALT",
            description = "Keep niche subreddits in an isolated space"
        ),
        AppTemplate(
            name = "ChatGPT",
            packageName = "com.openai.chatgpt",
            defaultUrl = "https://chatgpt.com",
            defaultBadgeColor = "#14B8A6",
            defaultBadge = "PRO",
            description = "Parallel AI workspace with dedicated session"
        ),
        AppTemplate(
            name = "Facebook",
            packageName = "com.facebook.katana",
            defaultUrl = "https://m.facebook.com",
            defaultBadgeColor = "#2563EB",
            defaultBadge = "2",
            description = "Simultaneous personal and marketplace profiles"
        ),
        AppTemplate(
            name = "Messenger",
            packageName = "com.facebook.orca",
            defaultUrl = "https://www.messenger.com",
            defaultBadgeColor = "#A855F7",
            defaultBadge = "WORK",
            description = "Dual chat inbox for page admins and clients"
        ),
        AppTemplate(
            name = "LinkedIn",
            packageName = "com.linkedin.android",
            defaultUrl = "https://www.linkedin.com",
            defaultBadgeColor = "#0A66C2",
            defaultBadge = "HR",
            description = "Run recruiting and executive identities concurrently"
        ),
        AppTemplate(
            name = "YouTube",
            packageName = "com.google.android.youtube",
            defaultUrl = "https://m.youtube.com",
            defaultBadgeColor = "#EF4444",
            defaultBadge = "SUB",
            description = "Isolated subscriptions and clean watch history"
        ),
        AppTemplate(
            name = "GitHub",
            packageName = "com.github.android",
            defaultUrl = "https://github.com",
            defaultBadgeColor = "#334155",
            defaultBadge = "DEV",
            description = "Separate enterprise org from personal open source"
        )
    )

    fun getInstalledApps(context: Context): List<InstalledAppInfo> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = pm.queryIntentActivities(intent, 0)
        val list = mutableListOf<InstalledAppInfo>()
        val seen = mutableSetOf<String>()

        for (info in resolveInfos) {
            val pkg = info.activityInfo.packageName
            if (pkg == context.packageName || seen.contains(pkg)) continue
            seen.add(pkg)

            val label = info.loadLabel(pm).toString()
            val icon = try {
                info.loadIcon(pm)
            } catch (e: Exception) {
                null
            }
            val isSystem = (info.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

            list.add(
                InstalledAppInfo(
                    packageName = pkg,
                    appName = label,
                    icon = icon,
                    isSystemApp = isSystem
                )
            )
        }

        return list.sortedBy { it.appName.lowercase() }
    }

    fun parseColor(hex: String, defaultColor: Color = Color(0xFF0284C7)): Color {
        return try {
            val clean = hex.removePrefix("#")
            if (clean.length == 6) {
                Color(android.graphics.Color.parseColor("#$clean"))
            } else if (clean.length == 8) {
                Color(android.graphics.Color.parseColor("#$clean"))
            } else {
                defaultColor
            }
        } catch (e: Exception) {
            defaultColor
        }
    }
}
