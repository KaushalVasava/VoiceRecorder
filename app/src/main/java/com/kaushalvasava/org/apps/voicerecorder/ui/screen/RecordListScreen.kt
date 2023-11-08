package com.kaushalvasava.org.apps.voicerecorder.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kaushalvasava.org.apps.voicerecorder.R
import com.kaushalvasava.org.apps.voicerecorder.model.AudioRecord
import com.kaushalvasava.org.apps.voicerecorder.ui.composables.RecordItem
import com.kaushalvasava.org.apps.voicerecorder.ui.theme.VoiceRecorderTheme
import com.kaushalvasava.org.apps.voicerecorder.viewModels.RecordingsViewModel

@ExperimentalMaterial3Api
@Composable
fun RecordingListScreen(
    navController: NavController,
    recordingsViewModel: RecordingsViewModel,
    onClick: (AudioRecord) -> Unit,
) {
    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }
    val recordings by
    recordingsViewModel.getRecordData(searchQuery)
        .collectAsState(initial = emptyList())
    Box(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    tint = Color.White,
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 16.dp, start = 8.dp, end = 16.dp)
                        .clickable {
                            navController.popBackStack()
                        }
                )
                SearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                    },
                    onSearch = {},
                    active = false,
                    onActiveChange = {},

                    placeholder = {
                        Text(stringResource(R.string.search_recording))
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = stringResource(R.string.search_recording)
                        )
                    },
                    trailingIcon = {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = stringResource(R.string.clear),
                            modifier = Modifier.clickable {
                                searchQuery = ""
                            }
                        )
                    }
                ) {

                }
            }
            LazyColumn(modifier = Modifier.padding(vertical = 8.dp)) {
                items(items = recordings) { record ->
                    RecordItem(audioRecord = record) {
                        onClick(record)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun RecordListPreview() {
    VoiceRecorderTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val recordingsViewModel: RecordingsViewModel = viewModel()
            RecordingListScreen(rememberNavController(), recordingsViewModel) {}
        }
    }
}


