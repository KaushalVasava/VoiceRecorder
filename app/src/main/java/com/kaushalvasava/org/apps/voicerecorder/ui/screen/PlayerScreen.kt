package com.kaushalvasava.org.apps.voicerecorder.ui.screen

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kaushalvasava.org.apps.voicerecorder.ui.composables.PlayerView
import com.kaushalvasava.org.apps.voicerecorder.viewModels.RecordingsViewModel

private fun play(path: String, mediaPlayer: MediaPlayer) {
    try {
        mediaPlayer.apply {
            setOnPreparedListener { mp -> mp.start() }
            setDataSource(path)
            prepareAsync()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    navController: NavController,
    recordingsViewModel: RecordingsViewModel,
    recordId: Int
) {
    LaunchedEffect(key1 = Unit) {
        recordingsViewModel.getRecordById(recordId)
    }
    val record by recordingsViewModel.recordID.collectAsState()

    var mediaPlayer: MediaPlayer? by remember {
        mutableStateOf(null)
    }
    if (record != null) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            Column(Modifier.background(Color.Black)) {
                TopAppBar(
                    title = {
                        Text(record?.filename ?: "")
                    },
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back",
                            Modifier
                                .padding(horizontal = 8.dp)
                                .clickable {
                                    navController.popBackStack()
                                })
                    }
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    PlayerView(
                        getSessionId = { mediaPlayer?.audioSessionId },
                        onPlay = {
                            if (mediaPlayer == null) {
                                mediaPlayer = MediaPlayer()
                                play(mediaPlayer = mediaPlayer!!, path = record!!.filePath)
                            } else {
                                mediaPlayer?.start()
                            }
                        }
                    ) {
                        mediaPlayer?.pause()
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = record!!.filename,
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                            ),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2,
                            modifier = Modifier.fillMaxWidth(0.90f)
                        )
                        Icon(
                            imageVector = Icons.Filled.Close,
                            tint = Color.LightGray,
                            contentDescription = "close",
                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    mediaPlayer?.stop()
                                    mediaPlayer?.release()
                                    mediaPlayer = null
                                }
                        )
                    }
                }
            }
        }
    } else {
        Box {
            Text("Loading..", modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PlayerPreview() {
    MaterialTheme {
//        PlayerScreen(
//            navController = rememberNavController(),
//            record = AudioRecord("Kaushal_refcird", "adsdkd", 0L, 0L)
//        )
    }
}