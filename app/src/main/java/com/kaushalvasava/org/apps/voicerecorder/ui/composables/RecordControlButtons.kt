package com.kaushalvasava.org.apps.voicerecorder.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kaushalvasava.org.apps.voicerecorder.R
import com.kaushalvasava.org.apps.voicerecorder.ui.theme.VoiceRecorderTheme

@Composable
fun RecordControlButtons(
    onRecord: () -> Unit = {},
    onStop: () -> Unit = {},
    onToggleRecord: () -> Unit = {},
) {
    var isPressedTogglePause by rememberSaveable {
        mutableStateOf(false)
    }

    var state by rememberSaveable {
        mutableStateOf(false)
    }
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(corner = CornerSize(8.dp))
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        RoundButton(
            modifier = Modifier.weight(1f),
            iconId = R.drawable.ic_close,
            iconSize = 55.dp,
            color = Color.Gray
        ) {
            onStop()
            state = true
        }
        Spacer(modifier = Modifier.height(8.dp))
        RoundButton(
            modifier = Modifier.weight(1f),
            iconId = if (isPressedTogglePause) {
                R.drawable.ic_pause
            } else {
                R.drawable.ic_play
            },
            iconSize = 65.dp,
            color = Color.Green
        ) {
            onRecord()
            isPressedTogglePause = !isPressedTogglePause
            state = true
        }
        Spacer(modifier = Modifier.height(8.dp))
        RoundButton(
            modifier = Modifier.weight(1f),
            iconId = if (state) {
                R.drawable.ic_done
            } else {
                R.drawable.ic_menu
            },
            iconSize = 55.dp,
            color = Color.Gray,
        ) {
            onToggleRecord()
            state = false
        }
    }

}


@Preview(showBackground = true)
@Composable
fun ControlButtonPreview() {
    VoiceRecorderTheme {
        Surface(
            modifier = Modifier.fillMaxWidth()
        ) {
            RecordControlButtons()
        }
    }
}