package com.kaushalvasava.org.apps.voicerecorder

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kaushalvasava.org.apps.voicerecorder.services.ACTION_START_FOREGROUND_SERVICE
import com.kaushalvasava.org.apps.voicerecorder.services.AudioService
import com.kaushalvasava.org.apps.voicerecorder.ui.composables.CircularTimerView
import com.kaushalvasava.org.apps.voicerecorder.ui.composables.RecordControlButtons
import com.kaushalvasava.org.apps.voicerecorder.ui.navhost.MyNavHost
import com.kaushalvasava.org.apps.voicerecorder.ui.theme.VoiceRecorderTheme
import com.kaushalvasava.org.apps.voicerecorder.utils.convertSecondsToHMmSs
import com.kaushalvasava.org.apps.voicerecorder.viewModels.RecordingsViewModel
import com.kaushalvasava.org.apps.voicerecorder.viewModels.TimerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var mService: AudioService? = null
    private var mBound: Boolean = false
    private val viewModel: TimerViewModel by viewModels()
    val recordingsViewModel: RecordingsViewModel by viewModels()

    // Audio recorder service related
    private var connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioService.AudioRecorderServiceBinder
            mService = binder.getService()
            viewModel.isConnected.value = mService != null
            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mService?.stopRecorderService()
            mBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        bindAudioServiceWithPermission()
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mService?.stopRecorderService()
        mService = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VoiceRecorderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val isConnect = viewModel.isConnected.collectAsState()
                    Log.d("TAG", "onCreate: ${isConnect.value}")
                    val navController = rememberNavController()
                    if (isConnect.value) {
                        MyNavHost(
                            navController = navController,
                            service = mService,
                            timerViewModel = viewModel,
                            recordingsViewModel = recordingsViewModel
                        )
                    }
                    //HomeScreen(navController = navController, mService = mService)

//                    Log.d("TAG", "onCreate: ${isConnect.value}")
//                    if (isConnect.value) {
//                    val showDialog = rememberSaveable { mutableStateOf(false) }
//                    if (showDialog.value)
//                        CustomDialog(
//                            navController = navController,
//                            value = "Recording_${System.currentTimeMillis()}",
//                            setShowDialog = {
//                                showDialog.value = it
//                            },
//                            mService = mService,
//                        ) {
//                        }
//
//                    Surface {
//                        Box(
//                            modifier = Modifier
//                                .background(Color.Black)
//                                .clip(RoundedCornerShape(16.dp)),
//                        ) {
//
//                        }
//                    }


//                    }
//                        HomeScreen(
//                            navController = navController,
//                            viewModel = viewModel,
//                            recordingViewModel = recordingViewModel,
//                            mService = mService,
//                        )
                }
            }
        }
    }

    private fun isPermissionGranted(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            permission
        ) != PackageManager.PERMISSION_GRANTED
    }

    private fun bindAudioServiceWithPermission() {
        val intent = Intent(applicationContext, AudioService::class.java)
        intent.action = ACTION_START_FOREGROUND_SERVICE
        if (isPermissionGranted(Manifest.permission.RECORD_AUDIO)
            && isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 0
                )
                startService(intent)
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        } else {
            startService(intent)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }
}

@Composable
fun MainContent(
    viewModel: TimerViewModel,
    onRecord: () -> Unit = {},
    onStop: () -> Unit = {},
    onToggleRecord: () -> Unit = {},
) {
    val time by viewModel.timerValue.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .clip(RoundedCornerShape(corner = CornerSize(8.dp))),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "${time?.convertSecondsToHMmSs()}",
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            color = Color.White
        )
        CircularTimerView(viewModel = viewModel)
        RecordControlButtons(
            onRecord = { onRecord() },
            onStop = {
                onStop()
            }
        ) {
            onToggleRecord()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    VoiceRecorderTheme {
        val viewModel: TimerViewModel = viewModel()
//        HomeScreen(
//            rememberNavController()
//        )
    }
}