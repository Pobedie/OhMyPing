package com.pobedie.ohmyping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pobedie.ohmyping.ui.theme.OhMyPingTheme
import com.pobedie.ohmyping.screen.MainScreen
import com.pobedie.ohmyping.service.NotificationCaptureService

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
