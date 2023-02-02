package com.kaushalvasava.org.apps.voicerecorder.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kaushalvasava.org.apps.voicerecorder.database.model.AudioRecord
import com.ysanjeet535.voicerecorder.utils.AppUtils

@Composable
fun RecordItem(audioRecord: AudioRecord, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(vertical = 4.dp)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundButton(
            iconId = com.kaushalvasava.org.apps.voicerecorder.R.drawable.ic_play,
            iconSize = 30.dp,
            color = Color.LightGray
        ) {
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = audioRecord.filename,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.White
            )
            Row(modifier = Modifier.align(Alignment.Start)) {
                Text(
                    text = audioRecord.duration.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppUtils.getDate(audioRecord.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        }
    }
}