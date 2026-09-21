package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.CloneInstance
import com.example.data.repository.CloneRepository
import com.example.util.InstalledAppHelper
import com.example.util.InstalledAppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CloneViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CloneRepository

    val allClones: StateFlow<List<CloneInstance>>
    val runningClones: StateFlow<List<CloneInstance>>

    private val _installedApps = MutableStateFlow<List<InstalledAppInfo>>(emptyList())
    val installedApps: StateFlow<List<InstalledAppInfo>> = _installedApps.asStateFlow()

    private val _isLoadingApps = MutableStateFlow(false)
    val isLoadingApps: StateFlow<Boolean> = _isLoadingApps.asStateFlow()

    // Dual Running State for simultaneous parallel instances
    private val _dualCloneA = MutableStateFlow<CloneInstance?>(null)
    val dualCloneA: StateFlow<CloneInstance?> = _dualCloneA.asStateFlow()

    private val _dualCloneB = MutableStateFlow<CloneInstance?>(null)
    val dualCloneB: StateFlow<CloneInstance?> = _dualCloneB.asStateFlow()

    private val _isDualSplitVertical = MutableStateFlow(true) // true = top/bottom, false = side-by-side
    val isDualSplitVertical: StateFlow<Boolean> = _isDualSplitVertical.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = CloneRepository(db.cloneDao())

        allClones = repository.allClones.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        runningClones = repository.runningClones.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.ensureDefaultClonesIfEmpty()
        }
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            _isLoadingApps.value = true
            val apps = withContext(Dispatchers.IO) {
                InstalledAppHelper.getInstalledApps(getApplication())
            }
            _installedApps.value = apps
            _isLoadingApps.value = false
        }
    }

    fun addClone(clone: CloneInstance) {
        viewModelScope.launch {
            repository.insertClone(clone)
        }
    }

    fun updateClone(clone: CloneInstance) {
        viewModelScope.launch {
            repository.updateClone(clone)
        }
    }

    fun deleteClone(clone: CloneInstance) {
        viewModelScope.launch {
            if (_dualCloneA.value?.id == clone.id) _dualCloneA.value = null
            if (_dualCloneB.value?.id == clone.id) _dualCloneB.value = null
            repository.deleteClone(clone)
        }
    }

    fun clearStorage(clone: CloneInstance) {
        viewModelScope.launch {
            repository.clearCloneStorage(clone.id)
        }
    }

    fun markCloneLaunched(cloneId: Long) {
        viewModelScope.launch {
            repository.recordLaunch(cloneId)
        }
    }

    fun stopClone(cloneId: Long) {
        viewModelScope.launch {
            repository.setRunningState(cloneId, false)
        }
    }

    fun stopAllClones() {
        viewModelScope.launch {
            repository.stopAllClones()
        }
    }

    fun setupDualRun(cloneA: CloneInstance, cloneB: CloneInstance) {
        _dualCloneA.value = cloneA
        _dualCloneB.value = cloneB
        viewModelScope.launch {
            repository.recordLaunch(cloneA.id)
            repository.recordLaunch(cloneB.id)
        }
    }

    fun setDualPaneA(clone: CloneInstance) {
        _dualCloneA.value = clone
        viewModelScope.launch { repository.recordLaunch(clone.id) }
    }

    fun setDualPaneB(clone: CloneInstance) {
        _dualCloneB.value = clone
        viewModelScope.launch { repository.recordLaunch(clone.id) }
    }

    fun swapDualPanes() {
        val a = _dualCloneA.value
        val b = _dualCloneB.value
        _dualCloneA.value = b
        _dualCloneB.value = a
    }

    fun toggleDualOrientation() {
        _isDualSplitVertical.value = !_isDualSplitVertical.value
    }
}
