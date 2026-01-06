package com.example.ohmyping

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ohmyping.ui.theme.OhMyPingTheme
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
