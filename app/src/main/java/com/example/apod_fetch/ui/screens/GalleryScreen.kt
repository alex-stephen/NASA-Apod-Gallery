package com.example.apod_fetch.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.apod_fetch.ApodTopAppBar
import com.example.apod_fetch.R
import com.example.apod_fetch.data.ApodPhoto
import com.example.apod_fetch.navigation.NavigationDestination
import com.example.apod_fetch.ui.ApodViewModel
import com.example.apod_fetch.ui.ApodViewModelFactory
import com.example.apod_fetch.utils.ApodNavigationType

object GalleryDestination : NavigationDestination {
    override val route = "gallery"
    override val titleRes = R.string.gallery_title
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    apodViewModel: ApodViewModel,
    navigateToPhotoDetails: (ApodPhoto) -> Unit,
    onNavigateUp: () -> Unit,
    navigationType: ApodNavigationType
){
    val apodUiState by apodViewModel.apodUiState.observeAsState()
    val apods by apodViewModel.apodsListState.observeAsState(emptyList())
    Log.d("DateUtils", "apodUIState: ${apodUiState?.startDate}")
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val filteredApods = remember(apodUiState, apods) {
        apods.filter { photo ->
            val photoDate = photo.date
            apodUiState?.let {
                photoDate <= it.endDate && photoDate >= it.startDate
            } ?: false
        }.reversed()
    }

    Scaffold(
        topBar = {
            ApodTopAppBar(
                title = stringResource(GalleryDestination.titleRes),
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = onNavigateUp
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.hubble_ultra_deep_field_high_rez_gallery),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            PhotosGridScreen(filteredApods, navigateToPhotoDetails, navigationType = navigationType)
        }
    }
}

@Composable
fun PhotosGridScreen(
    photos: List<ApodPhoto>,
    navigateToPhotoDetails: (ApodPhoto) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    navigationType: ApodNavigationType
) {
    val paddingForScreenSize: Dp
    if (navigationType == ApodNavigationType.BOTTOM_NAVIGATION ||
        navigationType == ApodNavigationType.NAVIGATION_RAIL) {
        paddingForScreenSize = 105.dp
    } else {
        paddingForScreenSize = 0.dp
    }

    Column(
        modifier = modifier.padding(top = 105.dp, bottom = paddingForScreenSize)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            modifier = Modifier.padding(horizontal = 4.dp),
            contentPadding = contentPadding,
        ) {
            items(items = photos, key = { photo -> photo.date }) { photo ->
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

@Composable
fun ApodPhotoSingle(photo: ApodPhoto, onClick: (ApodPhoto) -> Unit, modifier: Modifier = Modifier) {
    val displayUrl = if (photo.media_type == "video") photo.thumbnail_url ?: photo.url else photo.url
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        modifier = modifier
            .clickable { onClick(photo) },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Text(
            text = photo.date,
            color = Color.White,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(displayUrl)
                .crossfade(true)
                .build(),
            error = painterResource(R.drawable.ic_broken_image),
            placeholder = painterResource(R.drawable.loading_img),
            contentDescription = stringResource(R.string.apod_photo),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
        )
    }
}


