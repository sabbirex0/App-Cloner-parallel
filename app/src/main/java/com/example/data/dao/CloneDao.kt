package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CloneInstance
import kotlinx.coroutines.flow.Flow

@Dao
interface CloneDao {
    @Query("SELECT * FROM clone_instances ORDER BY lastLaunchedAt DESC, id DESC")
    fun getAllClones(): Flow<List<CloneInstance>>

    @Query("SELECT * FROM clone_instances WHERE id = :id LIMIT 1")
    fun getCloneById(id: Long): Flow<CloneInstance?>

    @Query("SELECT * FROM clone_instances WHERE id = :id LIMIT 1")
    suspend fun getCloneByIdDirect(id: Long): CloneInstance?

    @Query("SELECT * FROM clone_instances WHERE isRunning = 1")
    fun getRunningClones(): Flow<List<CloneInstance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClone(clone: CloneInstance): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClones(clones: List<CloneInstance>)

    @Update
    suspend fun updateClone(clone: CloneInstance)

    @Delete
    suspend fun deleteClone(clone: CloneInstance)

    @Query("DELETE FROM clone_instances WHERE id = :id")
    suspend fun deleteCloneById(id: Long)

    @Query("UPDATE clone_instances SET isRunning = :isRunning WHERE id = :id")
    suspend fun setRunningState(id: Long, isRunning: Boolean)

    @Query("UPDATE clone_instances SET isRunning = 0")
    suspend fun stopAllClones()

    @Query("UPDATE clone_instances SET lastLaunchedAt = :timestamp, launchCount = launchCount + 1, isRunning = 1 WHERE id = :id")
    suspend fun recordLaunch(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE clone_instances SET storageUsageKb = :usageKb WHERE id = :id")
    suspend fun updateStorageUsage(id: Long, usageKb: Long)

    @Query("UPDATE clone_instances SET storageUsageKb = 512 WHERE id = :id")
    suspend fun clearCloneStorage(id: Long)
}
