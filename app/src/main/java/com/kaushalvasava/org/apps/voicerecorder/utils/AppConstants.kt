package com.kaushalvasava.org.apps.voicerecorder.utils

import com.kaushalvasava.org.apps.voicerecorder.BuildConfig


object AppConstants {
    const val DATABASE_NAME = BuildConfig.APPLICATION_ID + "_recorder_db"
    const val DATE_FORMAT = "dd/MM/yy"
    const val AUDIO_FORMAT_M4A = "m4a"
    const val RECORDING_  = "recording_"
    object Notification {
        const val NOTIFICATION_ID = "com.kaushalvasava.org.apps.voicerecorder.notificationID"
        const val NOTIFICATION_CHANNEL_NAME = "audio_notification"
        const val NOTIFICATION_DESC = "Audio recorder notification"
        const val FOREGROUND_SERVICE_NOTIFICATION_ID = 124
    }

}