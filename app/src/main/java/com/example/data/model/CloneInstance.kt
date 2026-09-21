package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clone_instances")
data class CloneInstance(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val instanceName: String,
    val instanceNumber: Int = 2,
    val badgeColorHex: String = "#0284C7",
    val badgeLabel: String = "2",
    val cloneType: String = "SANDBOX_WEB", // "SANDBOX_WEB" or "NATIVE"
    val targetUrl: String? = null,
    val storageUsageKb: Long = 2560L,
    val isIncognito: Boolean = false,
    val isPinProtected: Boolean = false,
    val pinCode: String = "",
    val notes: String = "",
    val lastLaunchedAt: Long = System.currentTimeMillis(),
    val launchCount: Int = 0,
    val isRunning: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
