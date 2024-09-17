package com.example.lab08

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.lab08.ui.theme.Lab08Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab08Theme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("This is a special test file")
                    Spacer(modifier = Modifier.height(48.dp))
                    DisplayInternetImage("https://users.metropolia.fi/~jarkkov/folderimage.jpg")
                }
            }
        }
    }
}

@Composable
fun DisplayInternetImage(url: String) {
    var bitmap by rememberSaveable { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(url) {
        bitmap = bitmap ?: fetchImage(url)
    }

    bitmap?.let {
        Image(bitmap = it.asImageBitmap(), contentDescription = "Fetched Image")
    } ?: run {
        Text("Loading...")
    }
}

suspend fun fetchImage(url: String): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            (URL(url).openConnection() as HttpsURLConnection).inputStream.use { BitmapFactory.decodeStream(it) }
        }
        catch (e: Exception) {
            Log.e("ImageFetchError", "Failed to fetch image", e)
            null
        }
    }
}