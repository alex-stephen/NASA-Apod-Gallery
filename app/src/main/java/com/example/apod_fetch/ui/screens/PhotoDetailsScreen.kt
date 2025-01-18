package com.example.apod_fetch.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.apod_fetch.ApodTopAppBar
import com.example.apod_fetch.R
import com.example.apod_fetch.data.ApodPhoto
import com.example.apod_fetch.navigation.NavigationDestination
import com.example.apod_fetch.ui.ApodViewModel

object PhotoDetailsDestination : NavigationDestination {
    override val route = "photo_details"
    override val titleRes = R.string.photo_details_title
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailsScreen(
    photo: ApodPhoto?,
    onNavigateUp: () -> Unit,
    apodViewModel: ApodViewModel,
    modifier: Modifier = Modifier) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val context = LocalContext.current
    Scaffold(
        topBar = {
            ApodTopAppBar(
                title = stringResource(PhotoDetailsDestination.titleRes),
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = onNavigateUp
            ) },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = Color(0xFF517E5B),
                onClick = {

                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        if (photo != null) {
                            putExtra(Intent.EXTRA_TEXT, photo.url?: "No URL provided")
                        }
                        putExtra(Intent.EXTRA_SUBJECT, "Check out this NASA APOD photo!")
                    }
                    context.startActivity(
                        Intent.createChooser(shareIntent, "Share NASA APOD Photo")
                    )
                },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(bottom = 75.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.IosShare,
                    contentDescription = stringResource(R.string.share_photo_title)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.nasa),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(15.dp))
                photo?.let {
                    Text(text = "Title: ${it.title} " ?: "No Title",
                        modifier = Modifier.padding(10.dp))
                    Text(text = it.copyright?.let { "Copyright: $it" } ?: "No Copyright",
                        modifier = Modifier.padding(10.dp))
                    Text(text = "Date: ${it.date} " ?: "No Title",
                        modifier = Modifier.padding(10.dp))
                    val displayUrl =
                        if (it.media_type == "video") it.thumbnail_url ?: it.url else it.url
                    AsyncImage(
                        model = displayUrl,
                        contentDescription = it.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )

                    var isFavorite by remember { mutableStateOf(it.favourite) }

                    // Animate the color of the heart based on the `isFavorite` state
                    val heartColor by animateColorAsState(
                        targetValue = if (isFavorite) Color(0xFFE91E63) else Color(0xFF6650a4)
                    )
                    Button(
                        onClick = {
                            isFavorite = !isFavorite
                            apodViewModel.updateFavoriteStatus(it.copy(favourite = isFavorite))
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00331F)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF98D799)),
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(end = 10.dp)
                        ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            tint = heartColor,
                            contentDescription = stringResource(R.string.fav_title)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .background(color = Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Description: ${it.explanation}" ?: "No explanation provided.",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } ?: Text(text = "Photo not available")

                Spacer(modifier = Modifier.height(150.dp))
            }
        }
    }
}