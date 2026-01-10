package com.pobedie.ohmyping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pobedie.ohmyping.screen.MainApp
import com.pobedie.ohmyping.ui.theme.OhMyPingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            OhMyPingTheme {
                MainApp()
            }
        }
    }
}
