package com.kaushalvasava.org.apps.voicerecorder.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kaushalvasava.org.apps.voicerecorder.ui.theme.VoiceRecorderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(
    placeHolderMsg: String,
    state: MutableState<TextFieldValue>,
    modifier: Modifier,
) {

    TextField(
        value = state.value,
        modifier = modifier
            .clip(
                RoundedCornerShape(corner = CornerSize(16.dp))
            ).background(Color.Gray),
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        },
        placeholder = {
            Text(
                placeHolderMsg,
                color = Color.Black
            )
        },
        onValueChange = {
            state.value = it
        },
        singleLine = true
    )
}

@Preview
@Composable
fun SearchPreview() {
    VoiceRecorderTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
//            Search("Enter your name",  modifier = Modifier)
        }
    }
}