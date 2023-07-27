package com.example.nivasa

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


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


}