package com.kaushalvasava.org.apps.voicerecorder.ui.navhost

enum class State{
    HOME,
    RECORDINGS,
    PLAYER
}
sealed class NavigationItem(val route: String) {
    object Home : NavigationItem(State.HOME.name)
    object Recordings : NavigationItem(State.RECORDINGS.name)
    object Player : NavigationItem(State.PLAYER.name)
}