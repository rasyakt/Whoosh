package com.example.whoossh

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.whoossh.navigation.NavGraph
import com.example.whoossh.ui.theme.WhoosshTheme
import com.example.whoossh.viewmodel.BookingViewModel
import com.example.whoossh.utils.LocalLanguage
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import java.util.concurrent.atomic.AtomicReference

class MainActivity : FragmentActivity() {
    
    // Use AtomicReference for thread-safe deep link handling (P1 Issue #4)
    private val deepLinkIntentRef = AtomicReference<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Handle deep link from onCreate (cold start)
        handleDeepLink(intent)
        
        setContent {
            val bookingViewModel: BookingViewModel = viewModel()
            val currentLanguage by bookingViewModel.currentLanguage.collectAsState()
            
            CompositionLocalProvider(LocalLanguage provides currentLanguage) {
                WhoosshTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        val navController = rememberNavController()
                        NavGraph(
                            navController = navController,
                            viewModel = bookingViewModel,
                            deepLinkIntent = deepLinkIntentRef.get()
                        )
                    }
                }
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle deep link when app is already running
        Log.d("MainActivity", "onNewIntent called with action: ${intent.action}")
        handleDeepLink(intent)
        setIntent(intent)
    }
    
    private fun handleDeepLink(intent: Intent?) {
        if (intent?.action == Intent.ACTION_VIEW) {
            val data = intent.data
            Log.d("MainActivity", "Deep link detected: $data")
            // Thread-safe assignment (P1 Issue #4)
            deepLinkIntentRef.set(intent)
        }
    }
}