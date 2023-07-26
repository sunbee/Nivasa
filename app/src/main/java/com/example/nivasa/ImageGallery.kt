package com.example.nivasa

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter

@Composable
fun ImageGallery(modifier: Modifier, navController: NavController) {
    // Display a 2x2 grid of images using LazyVerticalGrid
    val viewModel = navController.previousBackStackEntry?.sharedViewModel<CameraViewModel>(navController = navController)
    val snaps = viewModel!!.snaps
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
    ) {
        items(snaps.value.size) { index ->
            // Calculate the image to display based on index and available images
            val imageUri = snaps.value[index]
            val painter: ImagePainter = rememberImagePainter(
                    data = imageUri,
                    builder = {
                        // You can apply transformations here if needed
                    })

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
