package com.kaushalvasava.org.apps.voicerecorder.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaushalvasava.org.apps.voicerecorder.model.AudioRecord
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

    val recordID = MutableStateFlow<AudioRecord?>(null)

    fun getRecordData(query: String?): Flow<List<AudioRecord>> {
        return if (query != null && query.isNotEmpty()) {
            repository.getAllAudioRecords(
                query
            )
        } else {
//            val list= mutableListOf<AudioRecord>()
//            for(i in 0..10){
//                list.add(AudioRecord("example $i","fdf", System.currentTimeMillis(),12L))
//            }
//            flowOf(list)
            repository.getRecords()
        }
    }
    fun getRecordById(recordId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            recordID.value = repository.getRecordById(recordId)
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
