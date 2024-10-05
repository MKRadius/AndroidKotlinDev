package com.example.lab15

import android.content.pm.PackageManager
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab15.ui.theme.Lab15Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewModelScope
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.io.IOException


class AppViewModel: ViewModel() {
    val isRecording = MutableLiveData<Boolean>(false)
    private val recordingsList = listOf("")

    val minBufferSize = AudioRecord.getMinBufferSize(
        44100,
        AudioFormat.CHANNEL_OUT_STEREO,
        AudioFormat.ENCODING_PCM_16BIT)
    val audioFormat = AudioFormat.Builder()
        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
        .setSampleRate(44100)
        .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
        .build()
    lateinit var audioRecord: AudioRecord

    suspend fun playAudio(iStream: InputStream) {
        // TO BE ADDED
    }

    fun startRecord(context: Context) {
        val recFileName = "record_test.raw"
        val storageDir =  context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        var recFile: File = File(storageDir.toString() + "/"+ recFileName)
        val audioData = ByteArray(minBufferSize)
        var outputStream = FileOutputStream(recFile)
        var dataOutputStream = DataOutputStream(BufferedOutputStream(outputStream))

        audioRecord = AudioRecord.Builder()
            .setAudioSource(MediaRecorder.AudioSource.MIC)
            .setAudioFormat(audioFormat)
            .setBufferSizeInBytes(minBufferSize)
            .build()

        if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
            Log.e("DBG", "AudioRecord initialization failed.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            audioRecord.startRecording()
            isRecording.postValue(true)

            try {
                while (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                    val read = audioRecord.read(audioData, 0, audioData.size)
                    if (read > 0) {dataOutputStream.write(audioData, 0, read) }
                }
                Log.d("DBG", "Recorder stopped")
            } catch (e: Exception) {
                Log.e("DBG", "Error: $e")
            } finally {
                dataOutputStream.close()
            }
        }
    }

    fun stopRecording() {
        Log.d("DBG", "Clicked stop")
        audioRecord.stop()
        audioRecord.release()
        isRecording.postValue(false)
    }
}

@Composable
fun App(model: AppViewModel) {
    val context = LocalContext.current
    val recordings = List(100) { "Recording #$it" }
    val isRecording: Boolean by model.isRecording.observeAsState(false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
           items(recordings) { recording ->
               Row { Text(recording) }
           }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = Color.Gray
        )

        Button(
            onClick = {
                if (!isRecording) model.startRecord(context)
                else model.stopRecording()
            },
            modifier = Modifier.padding(10.dp),
            colors = if (!isRecording) {
                ButtonDefaults.buttonColors(
                    containerColor = Color.Unspecified,
                    contentColor = Color.White
                )
            }
            else {
                ButtonDefaults.buttonColors(containerColor = Color.Red,contentColor = Color.White)
            }
        ) {
            if (!isRecording) { Text("Record") } else Text("Stop recording")
        }
    }
}

class MainActivity : ComponentActivity() {

    private fun hasPermissions(): Boolean {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Log.d("DBG", "No audio recorder access")
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 1)
            return true
        }
        return true
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            Lab15Theme {
                hasPermissions()
                App(viewModel())
            }
        }
    }
}
