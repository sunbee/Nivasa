package com.example.nivasa

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController

@Composable
fun NivasaApp() {
    val navController = rememberNavController()
    val TAG = "NIVASA_APP"

    NavHost(navController = navController, startDestination = "home") {
        navigation(
            startDestination = "preview",
            route = "camera_functionality"
        ) {
            composable("preview") {
                CameraScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Blue),
                    navController = navController
                    )
            }
            composable("gallery") {
                ImageGallery(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Magenta),
                    navController = navController
                )
            }  // end GALLERY
        }  // end NAVIGATION
    }  // end NAV HOST
}  // end NIVASA APP

@Composable
inline fun <reified T: ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    // Get parent route
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}