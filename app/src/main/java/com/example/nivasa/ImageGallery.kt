package com.example.nivasa

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter

@Composable
fun ImageGallery(
    images: MutableList<Uri?>,
    modifier: Modifier) {
    val TAG = "IMAGE_GALLERY"
    val context = LocalContext.current
    val resources = context.resources // Replace 'context' with the appropriate context (e.g., applicationContext or activity)
    val drawableResId = resources.getIdentifier("default_image", "drawable", context.packageName)
    val defaultImageUri = Uri.parse("android.resource://${context.packageName}/$drawableResId")
    Log.d(TAG, "Got Default URI: ${defaultImageUri}")
    // Display a 2x2 grid of images using LazyVerticalGrid
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
    ) {
        items(images.size + 1) { index ->
            // Calculate the image to display based on index and available images
            val imageUri = if (index < images.size) images[index] else null
            val painter: ImagePainter = if (imageUri != null) {
                rememberImagePainter(
                    data = imageUri,
                    builder = {
                        // You can apply transformations here if needed
                    }
                )
            } else {
                rememberImagePainter(
                    data = defaultImageUri,
                    builder = {
                        // You can apply transformations here if needed
                    }
                )
            }

            // Display the image in a square container
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(4.dp)
                    .background(Color.Green)
            ) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
