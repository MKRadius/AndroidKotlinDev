package com.example.lab15

import android.content.pm.PackageManager
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.icu.text.SimpleDateFormat
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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import kotlinx.coroutines.delay
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.util.Date
import java.util.Locale


class AppViewModel: ViewModel() {
    val currentPlayingAudio = MutableLiveData<String?>(null)
    val isPlaying = MutableLiveData<Boolean>(false)
    val isRecording = MutableLiveData<Boolean>(false)
    val recordingsList = MutableLiveData<List<String>>(null)

    val audioExtension = ".raw"
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
    lateinit var audioTrack: AudioTrack

    fun playAudio(context: Context, fileName: String) {
        isPlaying.postValue(true)
        currentPlayingAudio.postValue(fileName)

        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val file = File(storageDir, fileName.plus(audioExtension))

        if (!file.exists()) {
            Log.e("DBG", "Audio file ${fileName.plus(audioExtension)} does not exist.")
            return
        }

        val audioData = file.readBytes()
        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(44100)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                    .build()
            )
            .setBufferSizeInBytes(audioData.size)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(audioData, 0, audioData.size)

        viewModelScope.launch(Dispatchers.IO) {
            audioTrack.play()

            val totalLength = audioData.size / 4 // each sample is 2 bytes, stereo is 4

            while (isPlaying.value == true) {
                val playbackPosition = audioTrack.playbackHeadPosition

                if (playbackPosition >= totalLength) {
                    stopAudio()
                    break
                }
                delay(100) // Check every 100 ms
            }
        }
    }

    fun stopAudio() {
        audioTrack.stop()
        audioTrack.release()
        currentPlayingAudio.postValue(null)
        isPlaying.postValue(false)
    }

    fun updateRecordings(context: Context) {
        recordingsList.value = null
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)

        if (storageDir != null && storageDir.exists()) {
            val files = storageDir.listFiles()

            files?.let {
                val currentRecordings = recordingsList.value?.toMutableList() ?: mutableListOf()

                it.forEach { file ->
                    Log.d("DBG", "Found ${file.toString()}")
                    val recordingName = file.toString().substringAfterLast("/").substringBefore(audioExtension)
                    currentRecordings.add(recordingName)
                }

                currentRecordings.sortWith(compareBy {
                    it.substringAfter("recording_").toIntOrNull() ?: Int.MAX_VALUE
                })

                recordingsList.postValue(currentRecordings)
            }
        } else {
            Log.e("DBG", "Music directory not found or invalid.")
        }
    }


    fun createRecordingName(): String {
        val dateFormat = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(Date())

        val timeFormat = SimpleDateFormat("HHmmss", Locale.getDefault())
        val formattedTime = timeFormat.format(Date())

        return "recording_${formattedDate}${formattedTime}$audioExtension"
    }

    fun deleteRecordingFile(context: Context, fileName: String): Boolean {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)

        if (storageDir != null && storageDir.exists()) {
            val fileToDelete = File(storageDir, fileName)

            return if (fileToDelete.exists()) {
                val deleted = fileToDelete.delete()
                if (deleted) { Log.d("DBG", "File deleted: $fileName") }
                else { Log.e("DBG", "Failed to delete file: $fileName") }
                updateRecordings(context)
                deleted
            } else {
                Log.e("DBG", "File not found: $fileName")
                false
            }
        } else {
            Log.e("DBG", "Storage directory not found.")
            return false
        }
    }

    fun startRecord(context: Context) {
        val recFileName = createRecordingName()
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
    val recordings: List<String>? by model.recordingsList.observeAsState(null)
    val currentPlayingAudio: String? by model.currentPlayingAudio.observeAsState(null)
    val isPlaying: Boolean by model.isPlaying.observeAsState(false)
    val isRecording: Boolean by model.isRecording.observeAsState(false)

    model.updateRecordings(context)

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            recordings?.let {
                items(it) { recording ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .height(56.dp)
                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = recording,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        if (!isRecording) {
                            Icon(
                                imageVector = if ((!isPlaying && recording == currentPlayingAudio) || recording != currentPlayingAudio) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                                contentDescription = "Play",
                                tint = Color.DarkGray,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { if (!isPlaying) model.playAudio(context, recording) else model.stopAudio() },
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete",
                                tint = Color.Red,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clickable { model.deleteRecordingFile(context, recording.plus(model.audioExtension)) }
                            )
                        }
                    }
                }
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
            enabled = isPlaying == false,
            modifier = Modifier.padding(10.dp),
            shape = RoundedCornerShape(8.dp),
            colors = if (!isRecording) {
                ButtonDefaults.buttonColors(
                    containerColor = Color.Unspecified,
                    contentColor = Color.Black
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
