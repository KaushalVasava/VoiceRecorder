package com.kaushalvasava.org.apps.voicerecorder.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.kaushalvasava.org.apps.voicerecorder.R
import com.kaushalvasava.org.apps.voicerecorder.model.AudioRecord
import com.kaushalvasava.org.apps.voicerecorder.services.AudioService
import com.kaushalvasava.org.apps.voicerecorder.ui.composables.CircularTimerView
import com.kaushalvasava.org.apps.voicerecorder.ui.composables.RecordControlButtons
import com.kaushalvasava.org.apps.voicerecorder.ui.composables.SaveDialog
import com.kaushalvasava.org.apps.voicerecorder.ui.navhost.NavigationItem
import com.kaushalvasava.org.apps.voicerecorder.utils.AppConstants
import com.kaushalvasava.org.apps.voicerecorder.utils.convertSecondsToHMmSs
import com.kaushalvasava.org.apps.voicerecorder.viewModels.RecordingsViewModel
import com.kaushalvasava.org.apps.voicerecorder.viewModels.TimerViewModel
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    mService: AudioService?,
    timerViewModel: TimerViewModel,
    recordingsViewModel: RecordingsViewModel,
) {
    val context = LocalContext.current
    val time by timerViewModel.timerValue.collectAsState()
    val bottomSheet = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showDialog by rememberSaveable { mutableStateOf(false) }

    if (showDialog) {
        ModalBottomSheet(
            onDismissRequest = { showDialog = false },
            sheetState = bottomSheet,
        ) {
            SaveDialog(onSave = { name ->
                mService?.stopRecording()
                mService?.stopRecorderService()
                val filePath = mService?.getRecordedFilePath() ?: name
                val newFile = File("$name${AppConstants.AUDIO_FORMAT_MP3}")
                File(filePath).renameTo(newFile)
                val record = AudioRecord(
                    filename = name,
                    filePath = filePath,
                    date = System.currentTimeMillis(),
                    duration = time
                )
                timerViewModel.stopTimer()
                recordingsViewModel.insert(record)
                timerViewModel.addRecording(
                    AudioRecord(
                        AppConstants.RECORDING_ + System.currentTimeMillis(),
                        mService?.getRecordedFilePath() ?: error("File path not found"),
                        System.currentTimeMillis(),
                        time
                    )
                )
                navController.navigate(NavigationItem.Recordings.route)
                coroutineScope.launch {
                    bottomSheet.hide()
                }
                showDialog = false
            }) {
                coroutineScope.launch {
                    bottomSheet.hide()
                }
                showDialog = false
            }
        }
    }

    Column(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()
            .padding(8.dp)
            .clip(RoundedCornerShape(corner = CornerSize(8.dp))),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = time.convertSecondsToHMmSs(),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color.White
        )
        CircularTimerView(viewModel = timerViewModel)
        RecordControlButtons(
            onRecord = {
                val onPause = mService?.isPaused ?: false
                val recording = mService?.isRecordingStarted ?: false
                when {
                    onPause -> {
                        mService?.resumeRecorder()
                        timerViewModel.toggleTimer(false)
                    }

                    recording -> {
                        mService?.pauseRecorder()
                        timerViewModel.toggleTimer(true)
                    }

                    else -> {
                        mService?.startRecording()
                        timerViewModel.startTimer()
                    }
                }
            },
            onStop = {
                if (mService?.isRecordingStarted == true) {
                    timerViewModel.stopTimer()
                    mService.stopRecording()
                    mService.stopRecorderService()
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.recording_not_started_yet), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        ) {
            if (mService?.isRecordingStarted == true) {
                coroutineScope.launch {
                    bottomSheet.show()
                    showDialog = true
                }
            } else {
                navController.navigate(NavigationItem.Recordings.route)
            }
        }
    }
}