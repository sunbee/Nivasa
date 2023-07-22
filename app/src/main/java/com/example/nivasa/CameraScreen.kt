package com.example.nivasa

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.google.common.util.concurrent.ListenableFuture

@Composable
fun CameraPreview(
    modifier: Modifier,
    hasCameraPermission: Boolean,
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
) {
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (hasCameraPermission) {

            AndroidView(
                factory = { context ->
                    val previewView = PreviewView(context)
                    val preview = Preview.Builder().build()
                    val selector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                    try {
                        cameraProviderFuture.get().bindToLifecycle(
                            lifeCycleOwner,
                            selector,
                            preview
                        )
                    } catch(e: Exception) {
                        e.printStackTrace()
                    }
                    previewView
                }
            )
        }

    }
}