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
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.rememberNavController
import com.kaushalvasava.org.apps.voicerecorder.ui.theme.VoiceRecorderTheme
import com.kaushalvasava.org.apps.voicerecorder.services.ACTION_START_FOREGROUND_SERVICE
import com.kaushalvasava.org.apps.voicerecorder.services.AudioService
import com.kaushalvasava.org.apps.voicerecorder.ui.composables.CircularTimerView
import com.kaushalvasava.org.apps.voicerecorder.ui.composables.RecordControlButtons
import com.kaushalvasava.org.apps.voicerecorder.viewModels.RecordingsViewModel
import com.kaushalvasava.org.apps.voicerecorder.viewModels.TimerViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kaushalvasava.org.apps.voicerecorder.ui.screen.HomeScreen
import com.ysanjeet535.voicerecorder.utils.convertSecondsToHMmSs

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var mService: AudioService? = null
    private var mBound: Boolean = false
    private val viewModel: TimerViewModel by viewModels()

    //audio recorder service related
    private var connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioService.AudioRecorderServiceBinder
            mService = binder.getService()
            viewModel.isConnected.value = mService != null
            Log.d("TAG", "onServiceConnected")
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
//                    RequestPermission()
                    val recordingViewModel: RecordingsViewModel by viewModels()
                    val navController = rememberNavController()
                    val isConnect = viewModel.isConnected.collectAsState()
                    Log.d("TAG", "onCreate: ${isConnect.value}")
                    if (isConnect.value) {
                        HomeScreen(
                            navController = navController,
                            viewModel = viewModel,
                            recordingViewModel = recordingViewModel,
                            mService = mService,
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun RequestPermission() {
        val launcher: ManagedActivityResultLauncher<String, Boolean> =
            rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission Accepted: Do something
                    Log.d("TAG", "PERMISSION GRANTED")
                    startService(intent)
                    bindService(intent, connection, Context.BIND_AUTO_CREATE)
                }
            }

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                LocalContext.current,
                Manifest.permission.RECORD_AUDIO
            ) -> {
                // Some works that require permission
                Log.d("TAG", "Code requires permission")
                startService(intent)
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
            else -> {
                // Asking for permission
                SideEffect {
                    launcher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
        }

    }

    private fun bindAudioServiceWithPermission() {
        val intent = Intent(this, AudioService::class.java)
        intent.action = ACTION_START_FOREGROUND_SERVICE
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 0)
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
    val time by viewModel.timerValue.observeAsState()
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
            style = MaterialTheme.typography.headlineLarge,
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
        MainContent(
            viewModel = viewModel,
        )
    }
}