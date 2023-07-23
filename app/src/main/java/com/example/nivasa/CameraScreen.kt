package com.example.nivasa

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CameraPreview(
    modifier: Modifier,
    hasCameraPermission: Boolean
) {
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current
    /*
    * PreviewView: The PreviewView is provided by the CameraX library
    * and is used to display the camera preview on the screen.
    * Set up the Preview use case with preview.setSurfaceProvider
    * to show the camera feed within the PreviewView.
    * */
    val previewView = PreviewView(context)
    val preview = Preview.Builder().build()
    preview.setSurfaceProvider(previewView.surfaceProvider)

    val imageCapture = ImageCapture.Builder().build()

    val selector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (hasCameraPermission) {
            val cameraProviderFuture = remember {
                /*
                * CameraProviderFuture: The cameraProviderFuture is a ListenableFuture
                * that represents the asynchronous operation of obtaining the ProcessCameraProvider.
                * The future is used to retrieve the ProcessCameraProvider,
                * to use to bind the camera lifecycle.
                * */
                ProcessCameraProvider.getInstance(context)
            }  // Must be in composable scope

            /*
            * Handle the camera preview and binding the camera lifecycle using the CameraX library.
            * The AndroidView composable allows integration of the PreviewView
            * into the Jetpack Compose hierarchy seamlessly, and the Column composable
            * provides a neat and simple way to organize the UI elements.
            * */
            AndroidView(
                /*
                * AndroidView: The AndroidView composable is used to embed native Android views,
                * such as PreviewView, into Jetpack Compose. Inside the factory parameter of AndroidView,
                * create the PreviewView, set up the camera preview, and bind the camera lifecycle
                * using bindToLifecycle.*/
                factory = { context ->
                    cameraProviderFuture.get()?.unbindAll()
                    try {
                        /*
                        * try-catch: Properly handle any exceptions that might occur
                        * during the camera binding process using a try-catch block.
                        * If an exception is thrown, it is caught, Print the stack trace
                        * to diagnose any potential issues.
                        * */
                        cameraProviderFuture.get().bindToLifecycle(
                            lifeCycleOwner,
                            selector,
                            preview,
                            imageCapture
                        )
                    } catch(e: Exception) {
                        e.printStackTrace()
                    }
                    previewView
                }  // end FACTORY
            )  // end ANDROIDVIEW
        }  // end IF CAMERAHASPERMISSION
        // Button for capturing photos
        Button(
            onClick = { captureSnap(context, imageCapture) }, // Call the capturePhoto function on button click
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        ) {
            Text(text = "Capture Photo")
        }
    }  // end COLUMN
}

private fun captureSnap(context: Context, imageCapture: ImageCapture) {
    val TAG = "CAPTURE_SNAP"
    val outputDirectory = getOutputDirectory(context)
    val timeStamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(System.currentTimeMillis())
    val snapFile = File(outputDirectory, "IMG_${timeStamp}.jpg")

    val outputOptions = ImageCapture.OutputFileOptions.Builder(snapFile).build()

    imageCapture?.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            /*
            * Define an anonymous implementation of the ImageCapture.OnImageSavedCallback interface.
            * This interface is part of the CameraX library
            * and is used for handling the result of an image capture operation.
            * */
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                /*
                * This method is called when the image capture operation is successful.
                * It provides an ImageCapture.OutputFileResults object
                * that contains information about the saved image, such as the file path.
                * */
                val snapURI = Uri.fromFile(snapFile)
                Log.d(TAG, "Got snap with URI: $snapURI")
            }

            override fun onError(exception: ImageCaptureException) {
                /*
                * This method is called if an error occurs during the image capture operation.
                * It provides an ImageCaptureException object that contains details about the error.
                * */
                Log.e(TAG, "Captured No Snap! Error: ${exception.message}")
            }
            /*
            * By implementing this interface anonymously using object : ImageCapture.OnImageSavedCallback,
            * we have defined what should happen when the image is successfully saved (onImageSaved method)
            * and what should happen in case of an error (onError method).
            * */
        }  // end OBJECT
    )

}

private fun getOutputDirectory(context: Context): File {
    /*
    * Use ContextCompat.getExternalFilesDirs(context, Environment.DIRECTORY_PICTURES)
    * to get an array of directories where the app can save files,
    * specifically in the DIRECTORY_PICTURES. We then use firstOrNull
    * to find the first non-null directory that is mounted and available.
    * If mediaDir is not null, return it as the output directory.
    * Otherwise, we fallback to using the app's internal files directory (context.filesDir)
    * if there is no mounted external storage available.
    * */
    val mediaDirectories = ContextCompat.getExternalFilesDirs(context, Environment.DIRECTORY_PICTURES)
    val mediaDirectory = mediaDirectories.firstOrNull() {
        it != null && Environment.MEDIA_MOUNTED == Environment.getExternalStorageState(it)
    }
    return mediaDirectory ?: context.filesDir
}