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
import androidx.compose.foundation.layout.fillMaxWidth
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
import coil.annotation.ExperimentalCoilApi
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalCoilApi::class)
@Composable
fun CameraScreen(
    modifier: Modifier,
    uponSnapCaptured: (Uri) -> Unit
) {
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current

    val cameraProviderFuture = remember {
        /*
        * CameraProviderFuture: The cameraProviderFuture is a ListenableFuture
        * that represents the asynchronous operation of obtaining the ProcessCameraProvider.
        * The future is used to retrieve the ProcessCameraProvider,
        * to use to bind the camera lifecycle. See detailed appendix.
        * */
        ProcessCameraProvider.getInstance(context)
    }  // Must be in composable scope

    /*
    * PreviewView: The PreviewView is provided by the CameraX library
    * and is used to display the camera preview on the screen.
    * Set up the Preview use case with preview.setSurfaceProvider
    * to show the camera feed within the PreviewView.
    * */
    val previewView = PreviewView(context)
    val preview = Preview.Builder().build()
    preview.setSurfaceProvider(previewView.surfaceProvider)

    val imageCapture = remember {
        ImageCapture.Builder().build()
    }

    val selector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()

    /*
    * The AndroidView composable allows integration of the PreviewView
    * into the Jetpack Compose hierarchy seamlessly, and the Column composable
    * provides a neat and simple way to organize the UI elements.
    * */
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        AndroidView(
            /*
            * AndroidView: The AndroidView composable is used to embed native Android views,
            * such as PreviewView, into Jetpack Compose. Inside the factory parameter of AndroidView,
            * create the PreviewView, set up the camera preview, and bind the camera lifecycle
            * using bindToLifecycle.*/
            factory = { context ->
                //cameraProviderFuture.get()?.unbindAll()
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
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                previewView
            }  // end FACTORY
        )  // end ANDROIDVIEW

        // Button for capturing photos
        Button(
            onClick = {
                captureSnap(
                    context,
                    imageCapture,
                    uponSnapCaptured
                )
            }, // Call the capturePhoto function on button click
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        ) {
            Text(text = "Capture Photo")
        }  // end BUTTON
    }  // end COLUMN
}

private fun captureSnap(
    context: Context,
    imageCapture: ImageCapture,
    uponSnapCaptured: (Uri) -> Unit) {
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
                uponSnapCaptured(snapURI)
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

/* APPENDIX I: CAMERA USAGE IN ANDROID WITH PROVIDER & USE CASES
*
* `ProcessCameraProvider` is a crucial component in the CameraX library,
* and its role is to provide access to the camera and the necessary camera-related resources.
* It acts as a bridge between our app and the underlying camera hardware
* and allows us to bind different camera use cases to the camera device.
* Call `ProcessCameraProvider.getInstance() to obtain an instance
* of the `ProcessCameraProvider` class. This instance is essential
* for setting up and managing the camera and the associated use cases
* (e.g., preview, image capture, image analysis).
* Here's what happens when we get an instance of `ProcessCameraProvider` using `getInstance()`:
* 1. **Camera Initialization**: The `ProcessCameraProvider` is responsible
* for initializing the camera hardware and preparing it for use.
* When we call `ProcessCameraProvider.getInstance()`, the library initializes the camera,
* and if successful, it returns an instance of the `ProcessCameraProvider`.
* 2. **Camera Lifecycle Management**: CameraX manages the camera's lifecycle,
* allowing us to bind camera use cases to a specific lifecycle owner,
* typically an `Activity` or a `Fragment`. By using `bindToLifecycle()`
* with a `LifecycleOwner`, we ensure that the camera is released
* and resources are appropriately managed when the associated `LifecycleOwner` is destroyed.
* 3. **Use Case Management**: The `ProcessCameraProvider` allows us to bind various camera use cases
* to the camera device. We can set up and configure multiple use cases
* (e.g., `Preview`, `ImageCapture`, `ImageAnalysis`) independently
* and then bind them to the camera provider when needed.
* 4. *Future for Asynchronous Initialization**: The `getInstance()` method
* returns a `ListenableFuture<ProcessCameraProvider>`. A `ListenableFuture`
* is a part of the Guava library and is used to represent a future result
* that might not be available immediately. CameraX uses this future
* to asynchronously initialize the camera and provide the `ProcessCameraProvider` instance
* once it's ready. This is why we often see code that uses `.addListener()` or `.get()`
* to retrieve the `ProcessCameraProvider` instance.
*
* Here's an example of how to use `ProcessCameraProvider.getInstance()`:
* ```
* val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
*
* cameraProviderFuture.addListener({
*   // Camera initialization succeeded, and we have access to the ProcessCameraProvider
*   val cameraProvider = cameraProviderFuture.get()
*   // Use the cameraProvider to set up and bind camera use cases
*   // e.g., binding the Preview and ImageCapture use cases
* }, ContextCompat.getMainExecutor(context))
* ```
* In the code above, `ProcessCameraProvider.getInstance(context)` returns a `ListenableFuture`,
* and we use `addListener()` to register a callback that will be invoked
* when the future is complete (i.e., when the camera initialization is successful).
* Once the future is complete, we retrieve the `ProcessCameraProvider` instance
* using `cameraProviderFuture.get()` and proceed to set up and bind the camera use cases as needed.
*
* By obtaining the `ProcessCameraProvider` instance, we gain access to the core functionalities
* of CameraX, allowing us to interact with the camera hardware and configure various use cases
* to build your camera-related features in the app.
* */

