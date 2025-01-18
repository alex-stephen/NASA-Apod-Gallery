package com.example.apod_fetch.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apod_fetch.ApodTopAppBar
import com.example.apod_fetch.R
import com.example.apod_fetch.data.ApodPhoto
import com.example.apod_fetch.navigation.NavigationDestination
import com.example.apod_fetch.ui.ApodViewModel
import com.example.apod_fetch.ui.ApodViewModelFactory
import com.example.apod_fetch.utils.ApodNavigationType

object FavouritePhotosDestination : NavigationDestination {
    override val route = "favorites"
    override val titleRes = R.string.favourite_title
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteScreen(
    apodViewModelFactory: ApodViewModelFactory,
    navigateToPhotoDetails: (ApodPhoto) -> Unit,
    onNavigateUp: () -> Unit,
    navigationType: ApodNavigationType
){
    val apodViewModel: ApodViewModel = viewModel(factory = apodViewModelFactory)
    val apods by apodViewModel.apodsListState.observeAsState(emptyList())
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(Unit) {
        apodViewModel.getFavouritePhotos()
    }

    Scaffold(
        topBar = {
            ApodTopAppBar(
                title = stringResource(FavouritePhotosDestination.titleRes),
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = onNavigateUp
            )
        },
        ) {
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
            FavouritePhotosGridScreen(apods, navigateToPhotoDetails, navigationType = navigationType)
        }
    }
}

@Composable
fun FavouritePhotosGridScreen(
    photos: List<ApodPhoto>,
    navigateToPhotoDetails: (ApodPhoto) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    navigationType: ApodNavigationType
) {
    val paddingForScreenSize: Dp = if (navigationType == ApodNavigationType.BOTTOM_NAVIGATION) {
        105.dp
    } else {
        0.dp
    }

    val favouritePhotos = photos.filter { it.favourite }

    Column(
        modifier = modifier.padding(top = 105.dp, bottom = paddingForScreenSize)
    ) {
        if (favouritePhotos.isEmpty()) {

            Text(
                text = "No favourite photos available.",
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 60.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                modifier = Modifier.padding(horizontal = 4.dp),
                contentPadding = contentPadding,
            ) {
                items(items = favouritePhotos, key = { photo -> photo.date }) { photo ->
                    ApodPhotoSingle(
                        photo,
                        onClick = navigateToPhotoDetails,
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth()
                            .aspectRatio(1.5f)
                    )
                }
            }
        }
    }
}