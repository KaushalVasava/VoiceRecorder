package com.kaushalvasava.org.apps.voicerecorder.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.kaushalvasava.org.apps.voicerecorder.R
import com.kaushalvasava.org.apps.voicerecorder.ui.customViews.AudioVisualizerView

@Composable
fun PlayerView(
    getSessionId: () -> Int?,
    onPlay: () -> Unit,
    onPause: () -> Unit
) {

    var sessionId: Int? by remember {
        mutableStateOf(null)
    }
    var isPlaying by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(8.dp)
            .clip(
                RoundedCornerShape(corner = CornerSize(8.dp))
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val context = LocalContext.current
        if (sessionId != null) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                factory = {
                    AudioVisualizerView(context)
                }
            ) {
                it.apply {
                    setColor()
                    setDensityValue()
                    setPlayerId(sessionId)
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            RoundButton(
                modifier = Modifier.fillMaxWidth(),
                iconId = if (isPlaying) {
                    R.drawable.ic_pause
                } else {
                    R.drawable.ic_play
                },
                color = Color.Green,
            ) {
                isPlaying = if (isPlaying) {
                    onPause()
                    false
                } else {
                    onPlay()
                    sessionId = getSessionId()
                    true
                }
            }
        }
    }
}
