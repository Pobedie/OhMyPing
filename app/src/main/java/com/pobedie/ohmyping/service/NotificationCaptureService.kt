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
import com.pobedie.ohmyping.MainApp
import com.pobedie.ohmyping.R
import com.pobedie.ohmyping.database.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NotificationCaptureService : NotificationListenerService() {

    private lateinit var repository: AppRepository

    companion object {
        private const val TAG = "NotificationCaptureService"
        private const val CHANNEL_ID = "notification_listener"
        private const val NOTIFICATION_ID = 6969420

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

                enabledNotificationListeners?.contains(flattenedComponentName) == true
            } catch (e: Exception) {
                false
            }
        }
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        val appContainer = (application as MainApp).appContainer
        repository = appContainer.repository

        createNotificationChannel()
        Log.d(TAG, "NotificationCaptureService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        return START_STICKY // Restart if killed by system
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "NotificationCaptureService destroyed")
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "Notification listener connected")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d(TAG, "Notification listener disconnected")

        // Try to reconnect
        requestRebind(ComponentName(this, NotificationCaptureService::class.java))
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
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

        // Skip group summary notifications
        if (isGroupSummaryNotification(sbn)) {
            return
        }

        val notification = sbn.notification
        val extras = notification.extras

        // Extract notification data
        val nAppName = getAppName(packageName)
        val nTitle = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
        val nText = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
        val nSubText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()
        val nBigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
        val nChannelId = notification.channelId
        val notificationId = sbn.id

        val appRules = repository.activeRules.first()
        val serviceIsEnabled = repository.isListenerActive.first()
        val vibrator = applicationContext.getSystemService(Vibrator::class.java)

        if (serviceIsEnabled && appRules.any { it.packageName == packageName }) {
            val app = appRules.find { it.packageName == packageName } ?: return
            delay(1000) // to avoid overlapping with notification vibration
            if (nText != null && app.allChannels.triggerText.any { nText.contains(it) }) {
                serviceScope.launch {
                    vibrator.vibrate(
                        VibrationEffect.createWaveform(
                            app.allChannels.vibrationPattern.timings,
                            app.allChannels.vibrationPattern.amplitudes,
                            -1
                        )
                    )
                }
                return
            }

            if (nText != null) {
                app.namedChannels.forEach { _channel ->
                    if (nTitle!!.contains(_channel.name, true) ||
                        nChannelId.contains(_channel.name)) {
                        if (_channel.triggerText.any { nText.contains(it, true) } || _channel.triggerText.isEmpty()) {
                            serviceScope.launch {
                                vibrator.vibrate(
                                    VibrationEffect.createWaveform(
                                        _channel.vibrationPattern.timings,
                                        _channel.vibrationPattern.amplitudes,
                                        -1
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isGroupSummaryNotification(sbn: StatusBarNotification): Boolean {
        val notification = sbn.notification

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