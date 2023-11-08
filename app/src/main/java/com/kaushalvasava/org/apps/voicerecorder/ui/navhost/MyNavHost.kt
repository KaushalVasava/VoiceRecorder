package com.kaushalvasava.org.apps.voicerecorder.ui.navhost

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kaushalvasava.org.apps.voicerecorder.model.AudioRecord
import com.kaushalvasava.org.apps.voicerecorder.services.AudioService
import com.kaushalvasava.org.apps.voicerecorder.ui.screen.HomeScreen
import com.kaushalvasava.org.apps.voicerecorder.ui.screen.PlayerScreen
import com.kaushalvasava.org.apps.voicerecorder.ui.screen.RecordingListScreen
import com.kaushalvasava.org.apps.voicerecorder.viewModels.RecordingsViewModel
import com.kaushalvasava.org.apps.voicerecorder.viewModels.TimerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    timerViewModel: TimerViewModel,
    recordingsViewModel: RecordingsViewModel,
    startDestination: String = NavigationItem.Home.route,
    service: AudioService? = null
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationItem.Home.route) {
            HomeScreen(
                navController = navController,
                mService = service,
                timerViewModel,
                recordingsViewModel
            ) //{
//                navController.navigate("details/data=${it.title}")
//            }
        }
        composable(NavigationItem.Recordings.route) {
            RecordingListScreen(navController, recordingsViewModel) { record ->
//                navController.currentBackStackEntry?.savedStateHandle?.set("record", it)
                navController.navigate("${State.PLAYER.name}/data=${record.id}")
            }
        }
        composable("${State.PLAYER.name}/data={data}",
            arguments = listOf(
                navArgument("data") {
                    type = NavType.StringType
                }
            )) {
            val arguments = requireNotNull(it.arguments)
            val data = arguments.getString("data")?.toInt()!!
//            val news =
//                navController.previousBackStackEntry?.savedStateHandle?.get<AudioRecord>("record")
            PlayerScreen(navController, recordingsViewModel, data)
        }
    }
}