package com.kaushalvasava.org.apps.voicerecorder

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.kaushalvasava.org.apps.voicerecorder.utils.AppConstants
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VoiceRecorderApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    AppConstants.Notification.NOTIFICATION_ID,
                    AppConstants.Notification.NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}