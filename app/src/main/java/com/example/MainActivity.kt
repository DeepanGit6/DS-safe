package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.example.data.local.SafeDSDatabase
import com.example.data.repository.SafeDSRepository
import com.example.ui.SafeDSApp
import com.example.ui.SafeDSViewModel
import com.example.ui.SafeDSViewModelFactory
import com.example.ui.theme.SafeDSTheme

class MainActivity : ComponentActivity() {

    private val viewModel: SafeDSViewModel by viewModels {
        val database = SafeDSDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = SafeDSRepository(database.safeDSDao(), lifecycleScope)
        SafeDSViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            SafeDSTheme(darkTheme = isDarkMode) {
                SafeDSApp(viewModel = viewModel)
            }
        }
    }
}
