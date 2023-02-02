package com.kaushalvasava.org.apps.voicerecorder.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.Binder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kaushalvasava.org.apps.voicerecorder.R
import com.kaushalvasava.org.apps.voicerecorder.model.Action
import com.kaushalvasava.org.apps.voicerecorder.utils.AppConstants
import com.kaushalvasava.org.apps.voicerecorder.utils.AppConstants.Notification.FOREGROUND_SERVICE_NOTIFICATION_ID
import com.kaushalvasava.org.apps.voicerecorder.utils.AppConstants.RECORDING_
import com.kaushalvasava.org.apps.voicerecorder.utils.toast
import java.io.File
import java.io.IOException

//flags for using action received through notifications
const val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
const val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"
const val ACTION_START_RECORDING = "ACTION_START_RECORDING"
const val ACTION_STOP_RECORDING = "ACTION_STOP_RECORDING"
const val ACTION_PAUSE_RECORDING = "ACTION_PAUSE_RECORDING"

class AudioService : Service() {

    //Service binding
    private val audioRecorderServiceBinder = AudioRecorderServiceBinder()

    inner class AudioRecorderServiceBinder : Binder() {
        fun getService(): AudioService = this@AudioService
    }

    private var mediaRecorder: MediaRecorder? = null
    private var fileName: String? = null
    var isPaused: Boolean = false
    var isRecordingStarted: Boolean = false

    private val _isRecorded = MutableLiveData(false)
    val isRecorded: LiveData<Boolean> get() = _isRecorded

    override fun onBind(intent: Intent?): IBinder {
        fileName = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + Environment.DIRECTORY_DOWNLOADS + File.separator + RECORDING_ + System.currentTimeMillis()
                .toString() + AppConstants.AUDIO_FORMAT_M4A
        } else {
            "${externalCacheDir?.absolutePath}/$RECORDING_${System.currentTimeMillis()}${AppConstants.AUDIO_FORMAT_M4A}"
        }
        return audioRecorderServiceBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
            ACTION_START_FOREGROUND_SERVICE -> {
                startForegroundWithNotification()
            }

            ACTION_STOP_FOREGROUND_SERVICE -> {
                stopRecorderService()
            }

            ACTION_START_RECORDING -> {
                startRecording()
            }

            ACTION_STOP_RECORDING -> {
                stopRecording()
            }

            ACTION_PAUSE_RECORDING -> {
                togglePause()
            }

            else -> {
                getString(R.string.something_went_wrong)
            }
        }
        return START_STICKY
    }

    private fun startForegroundWithNotification() {

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else 0

        val stopIntent = Intent(this, AudioService::class.java)
        stopIntent.action = ACTION_STOP_FOREGROUND_SERVICE
        val stopPendingIntent =
            PendingIntent.getService(this, 123, stopIntent, flag)

        val stopRecordingIntent = Intent(this, AudioService::class.java)
        stopRecordingIntent.action = ACTION_STOP_RECORDING
        val stopRecordingPendingIntent =
            PendingIntent.getService(this, 123, stopRecordingIntent, flag)

        val startRecordingIntent = Intent(this, AudioService::class.java)
        startRecordingIntent.action = ACTION_START_RECORDING
        val startRecordingPendingIntent =
            PendingIntent.getService(this, 123, startRecordingIntent, flag)

        val pauseRecordingIntent = Intent(this, AudioService::class.java)
        pauseRecordingIntent.action = ACTION_STOP_FOREGROUND_SERVICE
        val pauseRecordingPendingIntent =
            PendingIntent.getService(this, 123, pauseRecordingIntent, flag)
        val builder = NotificationCompat.Builder(
            this, AppConstants.Notification.NOTIFICATION_ID,
        )
        builder.apply {
            setSmallIcon(R.mipmap.ic_launcher_round)
            setContentTitle(getString(R.string.app_name))
            setContentText(getString(R.string.notification_desc))
            addAction(
                NotificationCompat.Action(
                    0,
                    Action.EXIT.toString(),
                    stopRecordingPendingIntent
                )
            )
            addAction(
                NotificationCompat.Action(
                    0,
                    Action.PLAY.toString(),
                    startRecordingPendingIntent
                )
            )
            addAction(
                NotificationCompat.Action(
                    0,
                    Action.STOP.toString(),
                    pauseRecordingPendingIntent
                )
            )
        }

        val notification = builder.build()
        startForeground(FOREGROUND_SERVICE_NOTIFICATION_ID, notification)
    }

    //Audio recordings
    private fun initRecorder() {
        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(this)
        } else {
            @Suppress("deprecation")
            MediaRecorder()
        }
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setAudioEncodingBitRate(16 * 44100)
        mediaRecorder?.setAudioSamplingRate(96000)
        mediaRecorder?.setOutputFile(fileName)

        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                val file = fileName?.let { File(it) }
                file?.createNewFile()
            }
            mediaRecorder?.prepare()
            mediaRecorder?.start()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            toast {
                e.message.toString()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            toast {
                getString(R.string.give_write_permission)
            }
        }
    }

    fun startRecording() {
        _isRecorded.postValue(false)
        initRecorder()
        isRecordingStarted = true
    }

    fun stopRecording() {
        _isRecorded.postValue(true)
        mediaRecorder?.apply {
            stop()
            release()
        }
        isRecordingStarted = false
        mediaRecorder = null
    }

    fun pauseRecorder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mediaRecorder?.pause()
            isPaused = true
        }
    }

    fun resumeRecorder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mediaRecorder?.resume()
            isPaused = false
        }
    }

    private fun togglePause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!isPaused) {
                mediaRecorder?.pause()

            } else {
                mediaRecorder?.resume()
            }
        }
        isPaused = !isPaused
    }

    fun getRecordedFilePath(): String? {
        return fileName
    }

    fun stopRecorderService() {
        stopRecording()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
        stopSelf()
    }
}