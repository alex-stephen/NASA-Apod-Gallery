package com.example.apod_fetch.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.apod_fetch.data.ApodPhoto
import com.example.apod_fetch.ui.ApodViewModel
import com.example.apod_fetch.ui.ApodViewModelFactory
import com.example.apod_fetch.ui.screens.FavouritePhotosDestination
import com.example.apod_fetch.ui.screens.FavouriteScreen
import com.example.apod_fetch.ui.screens.GalleryDestination
import com.example.apod_fetch.ui.screens.GalleryScreen
import com.example.apod_fetch.ui.screens.GallerySelectDestination
import com.example.apod_fetch.ui.screens.GallerySelectScreen
import com.example.apod_fetch.ui.screens.HomeDestination
import com.example.apod_fetch.ui.screens.HomeScreen
import com.example.apod_fetch.ui.screens.PhotoDetailsDestination
import com.example.apod_fetch.ui.screens.PhotoDetailsScreen
import com.example.apod_fetch.ui.screens.RandomPhotosDestination
import com.example.apod_fetch.ui.screens.RandomPhotosScreen
import com.example.apod_fetch.utils.ApodNavigationType


/**
 * Provides Navigation graph for the application.
 */
@Composable
fun ApodNavHost(
    navController: NavHostController,
    apodViewModel: ApodViewModel,
    modifier: Modifier = Modifier,
    apodViewModelFactory: ApodViewModelFactory,
    navigationType: ApodNavigationType
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                onHomePhotoClick = { photo ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("apodPhoto", photo)
                    navController.navigate(PhotoDetailsDestination.route)
                },
                apodViewModel)
        }
        composable(route = GallerySelectDestination.route) {
            GallerySelectScreen(navigateToGallery = {navController.navigate(GalleryDestination.route)
            },
                onNavigateUp = { navController.navigateUp() },
                apodViewModel = apodViewModel,
                navigationType = navigationType)
        }
        composable(route = GalleryDestination.route) {
            GalleryScreen(
                apodViewModel = apodViewModel,
                navigateToPhotoDetails = { photo ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("apodPhoto", photo)
                    navController.navigate(PhotoDetailsDestination.route)
                },
                onNavigateUp = { navController.navigateUp() },
                navigationType = navigationType)
        }
        composable(route = PhotoDetailsDestination.route) {
            val photo = navController.previousBackStackEntry?.savedStateHandle?.get<ApodPhoto>("apodPhoto")
                PhotoDetailsScreen(
                    photo,
                    onNavigateUp = { navController.navigateUp() },
                    apodViewModel = apodViewModel)
        }
        composable(route = RandomPhotosDestination.route) {
            RandomPhotosScreen(
                apodViewModelFactory = apodViewModelFactory,
                navigateToPhotoDetails = { photo ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("apodPhoto", photo)
                    navController.navigate(PhotoDetailsDestination.route)
                },
                onNavigateUp = { navController.navigateUp() },
                navigationType = navigationType)
        }
        composable(route = FavouritePhotosDestination.route) {
            FavouriteScreen(
                apodViewModelFactory = apodViewModelFactory,
                navigateToPhotoDetails = { photo ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("apodPhoto", photo)
                    navController.navigate(PhotoDetailsDestination.route)
                },
                onNavigateUp = { navController.navigateUp() },
                navigationType = navigationType)
        }
    }
}