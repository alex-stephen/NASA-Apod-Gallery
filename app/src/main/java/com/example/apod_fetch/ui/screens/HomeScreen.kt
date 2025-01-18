package com.example.apod_fetch.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.util.convertByteToUUID
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.apod_fetch.ApodTopAppBar
import com.example.apod_fetch.R
import com.example.apod_fetch.data.ApodPhoto
import com.example.apod_fetch.navigation.NavigationDestination
import com.example.apod_fetch.navigation.NavigationItemContent
import com.example.apod_fetch.network.ApodApi
import com.example.apod_fetch.ui.ApodViewModel
import com.example.apod_fetch.ui.ApodViewModelFactory
import com.example.apod_fetch.ui.theme.APOD_FetchTheme
import com.example.apod_fetch.utils.ApodNavigationType
import com.example.apod_fetch.utils.DateUtils
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.home_title
}

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onHomePhotoClick: (ApodPhoto) -> Unit,
    apodViewModel: ApodViewModel,
    modifier: Modifier = Modifier
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val apod by apodViewModel.apodsState.observeAsState()

    LaunchedEffect(Unit) {
        apodViewModel.getApodPhotoOfTheDay()
    }
    Scaffold(
        topBar = {
            ApodTopAppBar(title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.hubble_ultra_deep_field_high_rez_home),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .width(450.dp)
                    .height(730.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Welcome to the Nasa Astronomy Picture of the Day",
                    textAlign = TextAlign.Center,
                    fontSize = 45.sp,
                    lineHeight = 60.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
                Spacer(modifier = Modifier.height(35.dp))

                apod?.let { PhotoOfTheDay(onClick = onHomePhotoClick, photo = it, Modifier) }
            }
        }
    }
}

@Composable
fun PhotoOfTheDay(onClick: (ApodPhoto) -> Unit, photo: ApodPhoto, modifier: Modifier) {
    val displayUrl = if (photo.media_type == "video") photo.thumbnail_url ?: photo.url else photo.url
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        modifier = modifier
            .clickable { onClick(photo) },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        photo.title?.let {
            Text(
                text = it,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
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
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
        )
        photo.date?.let {
            Text(
                text = it,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}