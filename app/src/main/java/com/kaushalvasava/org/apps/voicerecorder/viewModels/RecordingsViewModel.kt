package com.kaushalvasava.org.apps.voicerecorder.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaushalvasava.org.apps.voicerecorder.database.model.AudioRecord
import com.kaushalvasava.org.apps.voicerecorder.repo.RecorderRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordingsViewModel @Inject constructor(
    private val repository: RecorderRepo
) : ViewModel() {

    fun getRecordData(query: String?): Flow<List<AudioRecord>> {
        return if (query != null && query.isNotEmpty()) {
            repository.getAllAudioRecords(
                query
            )
        } else {
            repository.getRecords()
        }
    }

    fun insert(audioRecord: AudioRecord) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertRecording(audioRecord)
    }

    fun update(audioRecord: AudioRecord) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateRecording(audioRecord)
    }

    fun delete(audioRecord: AudioRecord) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteRecording(audioRecord)
    }

    suspend fun deleteAllRecordings() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAllAudioRecords()
    }
}
