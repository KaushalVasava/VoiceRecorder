package com.ysanjeet535.voicerecorder.utils

fun Long.convertSecondsToHMmSs(): String {
    val s = this % 60
    val m = this / 60 % 60
    val h = this / (60 * 60) % 24
    return String.format("%d:%02d:%02d", h, m, s)
}