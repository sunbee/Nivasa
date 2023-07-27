package com.example.nivasa

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class CameraViewModel: ViewModel() {

    val TAG = "VIEW_MODEL"

    // Replace this with a function to get the default image URI
    private fun getDefaultImageUri(): Uri {
        // Your logic to get the default image URI here
        return Uri.parse("android.resource://com.example.nivasa/${R.drawable.default_image}")
    }

    // Define a private MutableStateFlow for the list of image URIs
    val defaultImageCount = 4
    private val _snaps = MutableStateFlow(List(defaultImageCount) { getDefaultImageUri() })

    // Create a public read-only property for exposing the image URIs as StateFlow to the UI
    val snaps: StateFlow<List<Uri>> get() = _snaps.asStateFlow()

    // Function to update the list of image URIs
    fun updateSnaps(newSnap: Uri) {
        _snaps.value = _snaps.value.dropLast(1).toMutableList().apply { add(0, newSnap) }
        Log.d(TAG, "Got snaps: ${_snaps.value[0]}")
        if (_countSnaps.value < 4) _countSnaps.value++
    }

    private val _countSnaps = mutableStateOf(0)
    val countSnaps: State<Int> = _countSnaps

    fun sendImages(context: Context) {
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
        intent.type = "image/jpeg"

        val imageUriList = mutableListOf<Uri>()
        for (snap in snaps.value) {
            val file = snap.path?.let { File(it) }
            val uri = file?.let {
                FileProvider.getUriForFile(
                    context,
                    context.packageName + ".provider",
                    it
                )
            }
            if (uri != null) {
                imageUriList.add(uri)
            }
        }

        val parcelableList: ArrayList<out Parcelable> = ArrayList(imageUriList)

        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, parcelableList)
        context.startActivity(Intent.createChooser(intent, "Send images"))
    }
}