package com.kaushalvasava.org.apps.voicerecorder.ui.screen

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kaushalvasava.org.apps.voicerecorder.ui.composables.PlayerView
import com.kaushalvasava.org.apps.voicerecorder.ui.composables.RecordItem
import com.kaushalvasava.org.apps.voicerecorder.ui.composables.Search
import com.kaushalvasava.org.apps.voicerecorder.viewModels.RecordingsViewModel
import kotlinx.coroutines.launch

private fun play(path: String, mediaPlayer: MediaPlayer) {
    try {
        mediaPlayer.apply {
            setOnPreparedListener { mp -> mp.start() }
            setDataSource(path)
            prepareAsync()
        }
    } catch (e: Exception) {
        Log.d("TAG", "play: # ${e.message}")
        e.printStackTrace()
    }
}

@ExperimentalMaterial3Api
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayerBottomSheet(
    viewModel: RecordingsViewModel,
    navController: NavController,
) {
    val editTextState = remember {
        mutableStateOf(TextFieldValue(""))
    }
    Log.d("TAG", "PlayerBottomSheet: ${editTextState.value.text}")
    val recordings =
        viewModel.getRecordData(editTextState.value.text).collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState =
        BottomSheetState(BottomSheetValue.Collapsed)
    )
    var mediaPlayer: MediaPlayer? by remember {
        mutableStateOf(null)
    }
    var path by remember {
        mutableStateOf("")
    }
    var fileName by remember {
        mutableStateOf("")
    }
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.background(Color.Black)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = fileName,
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
                                    coroutineScope.launch {
                                        bottomSheetScaffoldState.bottomSheetState.collapse()
                                    }
                                }
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        PlayerView(
                            getSessionId = { mediaPlayer?.audioSessionId },
                            onPlay = {
                                if (mediaPlayer == null) {
                                    mediaPlayer = MediaPlayer()
                                    play(mediaPlayer = mediaPlayer!!, path = path)
                                } else {
                                    mediaPlayer?.start()
                                }
                            }
                        ) {
                            mediaPlayer?.pause()
                        }
                    }
                }
            }
        },
        sheetPeekHeight = 0.dp,
        content = {
            Box(
                modifier = Modifier
                    .background(Color.Black)
                    .padding(it)
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            tint = Color.White,
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back",
                            modifier = Modifier
                                .padding(top = 16.dp, bottom = 16.dp, start = 8.dp, end = 16.dp)
                                .clickable {
                                    navController.popBackStack()
                                }
                        )
                        Search(
                            placeHolderMsg = "Search recording",
                            state = editTextState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                        )
                    }
                    LazyColumn(modifier = Modifier.padding(vertical = 8.dp)) {
                        items(items = recordings.value) { record ->
                            RecordItem(record) {
                                path = record.filePath
                                fileName = record.filename
                                coroutineScope.launch {
                                    if (bottomSheetScaffoldState.bottomSheetState.isExpanded) {
                                        bottomSheetScaffoldState.bottomSheetState.collapse()
                                    } else {
                                        bottomSheetScaffoldState.bottomSheetState.expand()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
}