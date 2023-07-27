package com.example.nivasa

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter

@Composable
fun ImageGallery(modifier: Modifier, navController: NavController) {
    // Display a 2x2 grid of images using LazyVerticalGrid
    val cameraViewModel = navController.currentBackStackEntry?.sharedViewModel<CameraViewModel>(navController = navController)
    val snaps = cameraViewModel!!.snaps
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
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
                    )  // end IMAGE
                }  // end BOX
            }  // end FOR ITEM IN ITEMS
        }  // end LAZY VERTICAL GRID

        // Button for capturing photos
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .background(Color.Black)
        ) {
            Button(
                onClick = {
                    navController.navigate("preview")
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text("Back")
            }
            Button(
                onClick = { cameraViewModel?.sendImages(context) },
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text("Ship")
            }
        }  // end ROW
    }  // end COLUMN
}
