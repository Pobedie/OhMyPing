package com.pobedie.ohmyping.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import com.pobedie.ohmyping.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class NotificationCaptureService : NotificationListenerService() {


    companion object {
        private const val TAG = "NotificationCaptureService"
        private const val CHANNEL_ID = "notification_listener"
        private const val NOTIFICATION_ID = 696969

        fun startService(context: Context) {
            val intent = Intent(context, NotificationCaptureService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun isNotificationAccessGranted(context: Context, packageName: String): Boolean {
            return try {
                val componentName = ComponentName(packageName, NotificationCaptureService::class.java.name)
                val flattenedComponentName = componentName.flattenToString()

                // Get enabled notification listeners from system settings
                val enabledNotificationListeners = Settings.Secure.getString(
                    context.contentResolver,
                    "enabled_notification_listeners"
                )

                println("DEBUG enabledNotificationListeners :  ${enabledNotificationListeners}")
                enabledNotificationListeners?.contains(flattenedComponentName) == true
            } catch (e: Exception) {
                false
            }
        }
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.d(TAG, "NotificationCaptureService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("DEBUG onStartCommand ")
        startForeground(NOTIFICATION_ID, createNotification())
        return START_STICKY // Restart if killed by system
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "NotificationCaptureService destroyed")
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        println("DEBUG onListenerConnected ")
        Log.d(TAG, "Notification listener connected")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        println("DEBUG onListenerDisconnected ")
        Log.d(TAG, "Notification listener disconnected")

        // Try to reconnect
        requestRebind(ComponentName(this, NotificationCaptureService::class.java))
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        println("DEBUG onNotificationPosted ")
        serviceScope.launch {
            try {
                processNotification(sbn)
            } catch (e: Exception) {
                Log.e(TAG, "Error processing notification", e)
            }
        }
    }

    private suspend fun processNotification(sbn: StatusBarNotification) {
        val packageName = sbn.packageName

        // Skip our own notifications
        if (packageName == this.packageName) {
            return
        }

        // Skip system notifications that are not user-visible
        if (isSystemNotification(sbn)) {
            return
        }

        val notification = sbn.notification
        val extras = notification.extras

        // Extract notification data
        val appName = getAppName(packageName)
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
        val channelId = notification.channelId
        val notificationId = sbn.id

        if (text == "test") {
            val timings: LongArray = longArrayOf(
                50, 60,
                50, 60,
                50, 60,
                50, 60,
            )
            val amplitudes: IntArray = intArrayOf(
                250, 0,
                250, 0,
                250, 0,
                250, 0,
            )
            val repeatIndex = -1
            val vibrator = applicationContext.getSystemService(Vibrator::class.java)

            serviceScope.launch {
                vibrator.vibrate(
                    VibrationEffect.createWaveform(
                        timings, amplitudes, repeatIndex))
            }
        }
        Log.d(TAG, "Processing notification from $packageName:: $title : $channelId - $text")
        // Send notification here
    }

    private fun isSystemNotification(sbn: StatusBarNotification): Boolean {
        val notification = sbn.notification

        // Skip ongoing/persistent notifications
        if ((notification.flags and Notification.FLAG_ONGOING_EVENT) != 0) {
            return true
        }

        // Skip group summary notifications that don't have content
        if ((notification.flags and Notification.FLAG_GROUP_SUMMARY) != 0) {
            val hasContent = notification.extras.getCharSequence(Notification.EXTRA_TEXT) != null ||
                    notification.extras.getCharSequence(Notification.EXTRA_BIG_TEXT) != null
            if (!hasContent) {
                return true
            }
        }

        return false
    }


    private fun getAppName(packageName: String): String {
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            Log.w(TAG, "App name not found for package: $packageName")
            packageName
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Notification Listener Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Service that captures notifications"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID) // Use same CHANNEL_ID
            .setContentTitle("Notification Capture Service")
            .setContentText("Notification Listener")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setShowWhen(false)
            .build()
    }
}