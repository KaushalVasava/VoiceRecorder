package com.kaushalvasava.org.apps.voicerecorder.viewModels

import androidx.lifecycle.*
import com.kaushalvasava.org.apps.voicerecorder.model.AudioRecord
import com.kaushalvasava.org.apps.voicerecorder.repo.RecorderRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val repository: RecorderRepo
) : ViewModel() {

    val isConnected = MutableStateFlow(false)
    private val _timerValue = MutableStateFlow(0L)
    val timerValue: StateFlow<Long> get() = _timerValue

    private var startTime = 0L

    private var job: Job? = null

    fun addRecording(record: AudioRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertRecording(record)
        }
    }

    fun startTimer() {
        if (job?.isActive == true) {
            return
        }
        job = viewModelScope.launch {
            while (true) {
                delay(1000)
                startTime += 1
                _timerValue.value = startTime
            }
        }
        job!!.start()
    }

    fun stopTimer() {
        job?.cancel()
        startTime = 0
        _timerValue.value = 0
        if (job?.isCancelled == true) {
            job = null
        }
    }

    fun toggleTimer(isPaused: Boolean) {
        if (isPaused) {
            job?.cancel()
            _timerValue.value = startTime
        } else {
            job?.start()
            startTimer()
            _timerValue.value = startTime
        }
    }
}