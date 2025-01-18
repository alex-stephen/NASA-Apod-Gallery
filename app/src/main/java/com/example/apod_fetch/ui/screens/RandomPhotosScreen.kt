package com.example.apod_fetch.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.apod_fetch.ApodTopAppBar
import com.example.apod_fetch.R
import com.example.apod_fetch.data.ApodDatabase
import com.example.apod_fetch.data.ApodPhoto
import com.example.apod_fetch.data.ApodRepository
import com.example.apod_fetch.navigation.NavigationDestination
import com.example.apod_fetch.ui.ApodViewModel
import com.example.apod_fetch.ui.ApodViewModelFactory
import com.example.apod_fetch.utils.ApodNavigationType

object RandomPhotosDestination : NavigationDestination {
    override val route = "random"
    override val titleRes = R.string.random_title
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RandomPhotosScreen(
    apodViewModelFactory: ApodViewModelFactory,
    navigateToPhotoDetails: (ApodPhoto) -> Unit,
    onNavigateUp: () -> Unit,
    navigationType: ApodNavigationType
){
    val apodViewModel: ApodViewModel = viewModel(factory = apodViewModelFactory)
    val apods by apodViewModel.apodsListState.observeAsState(emptyList())
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    var isLoaded by remember { mutableStateOf(false) }

    if (isLoaded) {
        apodViewModel.getRandomPhotos(50)
        isLoaded = false
    }

    Scaffold(
        topBar = {
            ApodTopAppBar(
                title = stringResource(RandomPhotosDestination.titleRes),
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = onNavigateUp
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = Color(0xFF517E5B),
                onClick = {
                    isLoaded = !isLoaded
                },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(bottom = 75.dp)) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.random_photo_title)
                )
            }
        }) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.hubble_ultra_deep_field_high_rez_random),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            PhotosGridScreen(apods, navigateToPhotoDetails, navigationType = navigationType)
        }
    }
}