package com.example.runner

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.data.database.AppDatabase
import com.example.data.model.CloneInstance
import com.example.data.repository.CloneRepository
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class CloneRunnerActivity : ComponentActivity() {

    companion object {
        const val EXTRA_CLONE_ID = "extra_clone_id"

        fun createIntent(context: Context, cloneId: Long): Intent {
            return Intent(context, CloneRunnerActivity::class.java).apply {
                putExtra(EXTRA_CLONE_ID, cloneId)
                // Distinct document task so Android treats each clone as its own task window in Recents
                data = Uri.parse("clone://instance/$cloneId")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK or Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
            }
        }
    }

    private lateinit var repository: CloneRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val cloneId = intent.getLongExtra(EXTRA_CLONE_ID, -1L)
        val db = AppDatabase.getDatabase(this)
        repository = CloneRepository(db.cloneDao())

        if (cloneId > 0) {
            lifecycleScope.launch {
                repository.recordLaunch(cloneId)
            }
        }

        setContent {
            MyApplicationTheme {
                val cloneFlow = if (cloneId > 0) repository.getCloneById(cloneId) else flowOf(null)
                val clone by cloneFlow.collectAsState(initial = null)

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (clone != null) {
                        SandboxWebView(
                            clone = clone!!,
                            modifier = Modifier.padding(innerPadding),
                            showControls = true
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val cloneId = intent.getLongExtra(EXTRA_CLONE_ID, -1L)
        if (cloneId > 0) {
            lifecycleScope.launch {
                repository.setRunningState(cloneId, false)
            }
        }
    }
}
