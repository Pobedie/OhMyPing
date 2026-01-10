package com.pobedie.ohmyping

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pobedie.ohmyping.screen.MainApp
import com.pobedie.ohmyping.ui.theme.OhMyPingTheme
import com.pobedie.ohmyping.service.NotificationCaptureService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            OhMyPingTheme {
//                MainScreen()
                MainApp()
            }
        }
    }
}
