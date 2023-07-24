package com.example.nivasa

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.nivasa.ui.theme.NivasaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NivasaTheme {
                val context = LocalContext.current
                var hasCameraPermission by remember {
                    mutableStateOf(
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                }
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { granted ->
                        hasCameraPermission = granted
                    })
                LaunchedEffect(key1 = true, block = {
                    launcher.launch(Manifest.permission.CAMERA)
                })
                if (hasCameraPermission) {
                    NivasaApp()
                }
            }  // end NIVASA THEME
        }  // end SET CONTENT
    }  // end ON CREATE
}  // end MAIN ACTIVITY

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NivasaApp() {
    val images = remember { mutableStateListOf<Uri?>(null) }
    val TAG = "NIVASA_APP"

    Scaffold(
        topBar = { TopAppBar(title = { Text("Nivasa") }) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                ImageGallery(images = images)
                CameraScreen(
                    modifier = Modifier.height(150.dp), // Modifier.weight(1f),
                    uponSnapCaptured = { capturedImage ->
                        images.add(0, capturedImage)
                        Log.d(TAG, "Got images: ${images.size}")
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}
