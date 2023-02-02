package com.kaushalvasava.org.apps.voicerecorder.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppUtils {
    fun getDate(date: Long): String {
        return SimpleDateFormat(AppConstants.DATE_FORMAT, Locale.getDefault()).format(
            Date(
                date
            )
        )
    }
}

inline fun Context?.toast(crossinline msgProvider: () -> String) {
    this ?: return
    val block = {
        Toast.makeText(this, msgProvider(), Toast.LENGTH_SHORT).show()
    }
    postBlockInMainLooper(block)
}

fun postBlockInMainLooper(block: () -> Unit) {
    if (Looper.getMainLooper() == Looper.myLooper()) {
        block()
    } else {
        Handler(Looper.getMainLooper()).post(block)
    }
}
