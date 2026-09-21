package com.example.data.repository

import com.example.data.dao.CloneDao
import com.example.data.model.CloneInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class CloneRepository(private val cloneDao: CloneDao) {

    val allClones: Flow<List<CloneInstance>> = cloneDao.getAllClones()
    val runningClones: Flow<List<CloneInstance>> = cloneDao.getRunningClones()

    fun getCloneById(id: Long): Flow<CloneInstance?> = cloneDao.getCloneById(id)

    suspend fun getCloneByIdDirect(id: Long): CloneInstance? = cloneDao.getCloneByIdDirect(id)

    suspend fun insertClone(clone: CloneInstance): Long = cloneDao.insertClone(clone)

    suspend fun updateClone(clone: CloneInstance) = cloneDao.updateClone(clone)

    suspend fun deleteClone(clone: CloneInstance) = cloneDao.deleteClone(clone)

    suspend fun deleteCloneById(id: Long) = cloneDao.deleteCloneById(id)

    suspend fun setRunningState(id: Long, isRunning: Boolean) = cloneDao.setRunningState(id, isRunning)

    suspend fun stopAllClones() = cloneDao.stopAllClones()

    suspend fun recordLaunch(id: Long) = cloneDao.recordLaunch(id)

    suspend fun clearCloneStorage(id: Long) = cloneDao.clearCloneStorage(id)

    suspend fun updateStorageUsage(id: Long, usageKb: Long) = cloneDao.updateStorageUsage(id, usageKb)

    suspend fun ensureDefaultClonesIfEmpty() {
        val existing = cloneDao.getAllClones().first()
        if (existing.isEmpty()) {
            val defaults = listOf(
                CloneInstance(
                    packageName = "com.whatsapp",
                    appName = "WhatsApp",
                    instanceName = "WhatsApp (Personal)",
                    instanceNumber = 1,
                    badgeColorHex = "#10B981",
                    badgeLabel = "1",
                    cloneType = "SANDBOX_WEB",
                    targetUrl = "https://web.whatsapp.com",
                    storageUsageKb = 3450,
                    isIncognito = false,
                    isPinProtected = false,
                    notes = "Personal messages & family groups",
                    launchCount = 12
                ),
                CloneInstance(
                    packageName = "com.whatsapp",
                    appName = "WhatsApp",
                    instanceName = "WhatsApp (Work / Biz)",
                    instanceNumber = 2,
                    badgeColorHex = "#0284C7",
                    badgeLabel = "BIZ",
                    cloneType = "SANDBOX_WEB",
                    targetUrl = "https://web.whatsapp.com",
                    storageUsageKb = 4820,
                    isIncognito = false,
                    isPinProtected = true,
                    pinCode = "1234",
                    notes = "Client consultations & business inquiries",
                    launchCount = 28
                ),
                CloneInstance(
                    packageName = "org.telegram.messenger",
                    appName = "Telegram",
                    instanceName = "Telegram (Vault)",
                    instanceNumber = 2,
                    badgeColorHex = "#8B5CF6",
                    badgeLabel = "SEC",
                    cloneType = "SANDBOX_WEB",
                    targetUrl = "https://web.telegram.org/a/",
                    storageUsageKb = 2890,
                    isIncognito = false,
                    isPinProtected = true,
                    pinCode = "0000",
                    notes = "Secure encrypted cloud channels",
                    launchCount = 7
                ),
                CloneInstance(
                    packageName = "com.twitter.android",
                    appName = "X (Twitter)",
                    instanceName = "X (Tech & Dev Alt)",
                    instanceNumber = 2,
                    badgeColorHex = "#F59E0B",
                    badgeLabel = "ALT",
                    cloneType = "SANDBOX_WEB",
                    targetUrl = "https://x.com",
                    storageUsageKb = 1940,
                    isIncognito = false,
                    isPinProtected = false,
                    notes = "Secondary developer handle",
                    launchCount = 15
                )
            )
            cloneDao.insertClones(defaults)
        }
    }
}
