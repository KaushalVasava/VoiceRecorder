package com.kaushalvasava.org.apps.voicerecorder.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kaushalvasava.org.apps.voicerecorder.database.model.AudioRecord

@Database(entities = [AudioRecord::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract val dao: AudioRecordDao
}