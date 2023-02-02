package com.kaushalvasava.org.apps.voicerecorder.ui.composables

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.kaushalvasava.org.apps.voicerecorder.database.model.AudioRecord
import com.kaushalvasava.org.apps.voicerecorder.services.AudioService
import com.kaushalvasava.org.apps.voicerecorder.viewModels.RecordingsViewModel
import com.kaushalvasava.org.apps.voicerecorder.viewModels.TimerViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDialog(
    navController: NavController,
    value: String,
    setShowDialog: (Boolean) -> Unit,
    recordingViewModel: RecordingsViewModel,
    timerViewModel: TimerViewModel,
    mService: AudioService?,
    setValue: (String) -> Unit,
) {

    var txtFieldError by remember { mutableStateOf("") }
    var txtField by rememberSaveable { mutableStateOf(value) }

    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.Black
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Save recording",
                            color = Color.White,
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "",
                            tint = colorResource(android.R.color.darker_gray),
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)
                                .clickable {
                                    setShowDialog(false)
                                    Log.d("TAG", "CustomDialog: close")
                                }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    TextField(
                        modifier = Modifier.fillMaxWidth().background(Color.Black),
                        placeholder = {
                            Text(text = "Enter file name")
                        },
                        value = txtField,
                        onValueChange = {
                            txtField = it
                        })

                    Spacer(modifier = Modifier.height(20.dp))

                    Box(modifier = Modifier.padding(horizontal = 40.dp)) {
                        Button(
                            onClick = {
                                if (txtField.isEmpty()) {
                                    txtFieldError = "Field can not be empty"
                                    return@Button
                                }
                                setValue(txtField)
                                val fileName = mService?.getRecordedFilePath() ?: txtField
                                val newFile = File("$txtField.mp3")
                                File(fileName).renameTo(newFile)
                                val record = AudioRecord(
                                    filename = txtField,
                                    filePath = fileName,
                                    date = System.currentTimeMillis(),
                                    duration = timerViewModel.timerValue.value ?: 0L
                                )
                                recordingViewModel.insert(record)
                                navController.navigate("setting_screen")
                                setShowDialog(false)
                            },
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(text = "Done")
                        }
                    }
                }
            }
        }
    }
}