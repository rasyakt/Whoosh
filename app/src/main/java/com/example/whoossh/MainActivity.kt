package com.example.whoossh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.whoossh.navigation.WhooshNavGraph
import com.example.whoossh.ui.theme.WhoosshTheme
import com.example.whoossh.viewmodel.BookingViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WhoosshTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val bookingViewModel: BookingViewModel = viewModel()
                    WhooshNavGraph(
                        navController = navController,
                        viewModel = bookingViewModel
                    )
                }
            }
        }
    }
}