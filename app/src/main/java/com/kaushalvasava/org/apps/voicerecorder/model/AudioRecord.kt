package com.kaushalvasava.org.apps.voicerecorder.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Entity(tableName = "audioRecords")
data class AudioRecord(
    var filename: String,
    var filePath: String,
    var date: Long,
    var duration: Long
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}