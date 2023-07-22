package com.example.nivasa

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.google.common.util.concurrent.ListenableFuture

@Composable
fun CameraPreview(
    modifier: Modifier,
    hasCameraPermission: Boolean
) {
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (hasCameraPermission) {

            val cameraProviderFuture = remember {
                ProcessCameraProvider.getInstance(context)
            }  // Must be in composable scope

            AndroidView(
                factory = { context ->
                    val previewView = PreviewView(context)
                    val preview = Preview.Builder().build()
                    val selector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()
                    preview.setSurfaceProvider(previewView.surfaceProvider)

                    cameraProviderFuture.get()?.unbindAll()
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
                }  // end FACTORY
            )  // end ANDROIDVIEW
        }  // end IF CAMERAHASPERMISSION
    }  // end COLUMN
}