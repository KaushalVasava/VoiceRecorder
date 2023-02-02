package com.kaushalvasava.org.apps.voicerecorder.repo

import com.kaushalvasava.org.apps.voicerecorder.database.model.AudioRecord
import kotlinx.coroutines.flow.Flow

interface RecorderRepo {

    suspend fun insertRecording(recording: AudioRecord)

    suspend fun deleteRecording(recording: AudioRecord)

    suspend fun updateRecording(recording: AudioRecord)

    fun getRecords(): Flow<List<AudioRecord>>

    fun getAllAudioRecords(
        searchQuery: String
    ): Flow<List<AudioRecord>>

    suspend fun deleteAllAudioRecords()
}