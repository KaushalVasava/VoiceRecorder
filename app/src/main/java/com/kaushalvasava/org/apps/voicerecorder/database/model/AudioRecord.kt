package com.kaushalvasava.org.apps.voicerecorder.database.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "audioRecords")
data class AudioRecord (
    var filename: String,
    var filePath: String,
    var date: Long,
    var duration: Long
){

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}