package com.example.ohmyping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ohmyping.ui.theme.OhMyPingTheme
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat.startForeground
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.getSystemService
import com.example.ohmyping.screen.MainScreen
import com.example.ohmyping.service.NotificationCaptureService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        if (NotificationCaptureService.isNotificationAccessGranted(applicationContext, this.packageName)) {
//            NotificationCaptureService.startService(applicationContext)
//        } else {
//            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
//            startActivity(intent)
//        }
        println("IS NOTIFICATION PERMISSION GRANTED: ${NotificationCaptureService.isNotificationAccessGranted(applicationContext, this.packageName)}")

        setContent {
            OhMyPingTheme {
                MainScreen()
            }
        }
    }
}
