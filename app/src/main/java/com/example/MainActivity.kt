package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.CloneInstance
import com.example.runner.CloneRunnerActivity
import com.example.ui.components.CloneDetailSheet
import com.example.ui.components.CreateCloneSheet
import com.example.ui.components.InstalledAppsPickerSheet
import com.example.ui.components.PinLockDialog
import com.example.ui.screens.DualRunnerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SingleRunnerScreen
import com.example.ui.screens.StorageManagerScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CloneViewModel
import com.example.util.AppTemplate
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    DUAL_RUNNER,
    SINGLE_RUNNER,
    STORAGE_MANAGER
}

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val viewModel: CloneViewModel = viewModel()
                val allClones by viewModel.allClones.collectAsState()
                val runningClones by viewModel.runningClones.collectAsState()
                val installedApps by viewModel.installedApps.collectAsState()
                val isLoadingApps by viewModel.isLoadingApps.collectAsState()

                val dualCloneA by viewModel.dualCloneA.collectAsState()
                val dualCloneB by viewModel.dualCloneB.collectAsState()
                val isVerticalSplit by viewModel.isDualSplitVertical.collectAsState()

                var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
                var singleRunningClone by remember { mutableStateOf<CloneInstance?>(null) }

                var selectedCloneForDetail by remember { mutableStateOf<CloneInstance?>(null) }
                var showCreateSheet by remember { mutableStateOf(false) }
                var templateForCreate by remember { mutableStateOf<AppTemplate?>(null) }
                var showAppPickerSheet by remember { mutableStateOf(false) }

                var pinLockedClone by remember { mutableStateOf<CloneInstance?>(null) }
                var pendingActionAfterUnlock by remember { mutableStateOf<(() -> Unit)?>(null) }

                val scope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }

                val createSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                val pickerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

                fun executeWithPinCheck(clone: CloneInstance, action: () -> Unit) {
                    if (clone.isPinProtected && clone.pinCode.isNotBlank()) {
                        pinLockedClone = clone
                        pendingActionAfterUnlock = action
                    } else {
                        action()
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        contentWindowInsets = WindowInsets.safeDrawing,
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        BoxContainer(modifier = Modifier.padding(innerPadding)) {
                            when (currentScreen) {
                                AppScreen.HOME -> {
                                    HomeScreen(
                                        clones = allClones,
                                        runningClones = runningClones,
                                        onCloneClick = { clone ->
                                            executeWithPinCheck(clone) {
                                                singleRunningClone = clone
                                                viewModel.markCloneLaunched(clone.id)
                                                currentScreen = AppScreen.SINGLE_RUNNER
                                            }
                                        },
                                        onCloneMoreClick = { clone ->
                                            selectedCloneForDetail = clone
                                        },
                                        onLaunchDualSplit = { a, b ->
                                            viewModel.setupDualRun(a, b)
                                            currentScreen = AppScreen.DUAL_RUNNER
                                        },
                                        onOpenDualRunner = {
                                            if (allClones.isNotEmpty()) {
                                                val first = allClones.first()
                                                val second = if (allClones.size > 1) allClones[1] else allClones.first()
                                                viewModel.setupDualRun(first, second)
                                                currentScreen = AppScreen.DUAL_RUNNER
                                            } else {
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Create at least one clone first")
                                                }
                                            }
                                        },
                                        onOpenStorageManager = {
                                            currentScreen = AppScreen.STORAGE_MANAGER
                                        },
                                        onCreateClone = {
                                            templateForCreate = null
                                            showCreateSheet = true
                                        },
                                        onTemplateSelect = { tpl ->
                                            templateForCreate = tpl
                                            showCreateSheet = true
                                        }
                                    )
                                }

                                AppScreen.DUAL_RUNNER -> {
                                    DualRunnerScreen(
                                        cloneA = dualCloneA,
                                        cloneB = dualCloneB,
                                        allClones = allClones,
                                        isVerticalSplit = isVerticalSplit,
                                        onSelectPaneA = { viewModel.setDualPaneA(it) },
                                        onSelectPaneB = { viewModel.setDualPaneB(it) },
                                        onSwapPanes = { viewModel.swapDualPanes() },
                                        onToggleOrientation = { viewModel.toggleDualOrientation() },
                                        onClose = { currentScreen = AppScreen.HOME }
                                    )
                                }

                                AppScreen.SINGLE_RUNNER -> {
                                    val clone = singleRunningClone
                                    if (clone != null) {
                                        SingleRunnerScreen(
                                            clone = clone,
                                            onBack = { currentScreen = AppScreen.HOME },
                                            onLaunchDualWithThis = { thisClone ->
                                                val other = allClones.firstOrNull { it.id != thisClone.id } ?: thisClone
                                                viewModel.setupDualRun(thisClone, other)
                                                currentScreen = AppScreen.DUAL_RUNNER
                                            },
                                            onLaunchSystemMultiTask = { thisClone ->
                                                val intent = CloneRunnerActivity.createIntent(this@MainActivity, thisClone.id)
                                                startActivity(intent)
                                            }
                                        )
                                    } else {
                                        currentScreen = AppScreen.HOME
                                    }
                                }

                                AppScreen.STORAGE_MANAGER -> {
                                    StorageManagerScreen(
                                        clones = allClones,
                                        onClearStorage = { clone ->
                                            viewModel.clearStorage(clone)
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Cleared storage for ${clone.instanceName}")
                                            }
                                        },
                                        onStopAllClones = {
                                            viewModel.stopAllClones()
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Stopped all running instances")
                                            }
                                        },
                                        onBack = { currentScreen = AppScreen.HOME }
                                    )
                                }
                            }
                        }

                        // Create Clone Sheet
                        if (showCreateSheet) {
                            CreateCloneSheet(
                                sheetState = createSheetState,
                                initialTemplate = templateForCreate,
                                onPickInstalledApp = {
                                    viewModel.loadInstalledApps()
                                    showAppPickerSheet = true
                                },
                                onSaveClone = { newClone ->
                                    viewModel.addClone(newClone)
                                    showCreateSheet = false
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Created ${newClone.instanceName}")
                                    }
                                },
                                onDismiss = { showCreateSheet = false }
                            )
                        }

                        // Clone Detail / Options Sheet
                        selectedCloneForDetail?.let { clone ->
                            CloneDetailSheet(
                                clone = clone,
                                sheetState = detailSheetState,
                                onLaunchSingle = {
                                    selectedCloneForDetail = null
                                    executeWithPinCheck(clone) {
                                        singleRunningClone = clone
                                        viewModel.markCloneLaunched(clone.id)
                                        currentScreen = AppScreen.SINGLE_RUNNER
                                    }
                                },
                                onLaunchDual = {
                                    selectedCloneForDetail = null
                                    executeWithPinCheck(clone) {
                                        val other = allClones.firstOrNull { it.id != clone.id } ?: clone
                                        viewModel.setupDualRun(clone, other)
                                        currentScreen = AppScreen.DUAL_RUNNER
                                    }
                                },
                                onLaunchSystemMultiTask = {
                                    selectedCloneForDetail = null
                                    executeWithPinCheck(clone) {
                                        val intent = CloneRunnerActivity.createIntent(this@MainActivity, clone.id)
                                        startActivity(intent)
                                    }
                                },
                                onClearStorage = {
                                    viewModel.clearStorage(clone)
                                    selectedCloneForDetail = null
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Storage cleared for ${clone.instanceName}")
                                    }
                                },
                                onDeleteClone = {
                                    viewModel.deleteClone(clone)
                                    selectedCloneForDetail = null
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Deleted ${clone.instanceName}")
                                    }
                                },
                                onUpdateClone = { updated ->
                                    viewModel.updateClone(updated)
                                    selectedCloneForDetail = updated
                                },
                                onDismiss = { selectedCloneForDetail = null }
                            )
                        }

                        // Installed Apps Picker Sheet
                        if (showAppPickerSheet) {
                            InstalledAppsPickerSheet(
                                sheetState = pickerSheetState,
                                apps = installedApps,
                                isLoading = isLoadingApps,
                                onSelectApp = { app ->
                                    val newClone = CloneInstance(
                                        packageName = app.packageName,
                                        appName = app.appName,
                                        instanceName = "${app.appName} (Instance 2)",
                                        instanceNumber = 2,
                                        badgeColorHex = "#7C3AED",
                                        badgeLabel = "2",
                                        cloneType = "NATIVE",
                                        targetUrl = "https://m.google.com/search?q=${app.appName}",
                                        storageUsageKb = 2048L
                                    )
                                    viewModel.addClone(newClone)
                                    showAppPickerSheet = false
                                    showCreateSheet = false
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Cloned ${app.appName} successfully!")
                                    }
                                },
                                onDismiss = { showAppPickerSheet = false }
                            )
                        }

                        // PIN Lock Dialog
                        pinLockedClone?.let { clone ->
                            PinLockDialog(
                                clone = clone,
                                onSuccess = {
                                    pinLockedClone = null
                                    val action = pendingActionAfterUnlock
                                    pendingActionAfterUnlock = null
                                    action?.invoke()
                                },
                                onDismiss = {
                                    pinLockedClone = null
                                    pendingActionAfterUnlock = null
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BoxContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.Box(modifier = modifier.fillMaxSize()) {
        content()
    }
}
