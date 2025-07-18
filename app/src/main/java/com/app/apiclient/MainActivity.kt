package com.app.apiclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.app.apiclient.ui.navigation.ApiClientNavigation
import com.app.apiclient.ui.theme.ApiClientTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ApiClientTheme {
                ApiClientNavigation()
            }
        }
    }
}