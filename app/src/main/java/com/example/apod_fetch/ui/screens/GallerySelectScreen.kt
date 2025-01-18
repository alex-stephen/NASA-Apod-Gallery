/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.apod_fetch.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.apod_fetch.ApodTopAppBar
import com.example.apod_fetch.R
import com.example.apod_fetch.navigation.NavigationDestination
import com.example.apod_fetch.ui.ApodViewModel
import com.example.apod_fetch.ui.theme.APOD_FetchTheme
import com.example.apod_fetch.utils.ApodNavigationType
import com.example.apod_fetch.utils.DateUtils
import java.time.LocalDate
import java.time.Year

object GallerySelectDestination : NavigationDestination {
    override val route = "select"
    override val titleRes = R.string.gallery_select_title
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("NewApi", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GallerySelectScreen(
    navigateToGallery: () -> Unit,
    onNavigateUp: () -> Unit,
    apodViewModel: ApodViewModel,
    navigationType: ApodNavigationType,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            ApodTopAppBar(
                title = stringResource(GallerySelectDestination.titleRes),
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = onNavigateUp
            )

        }) {

        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.hubble_ultra_deep_field_high_rez_galleryselect),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .width(450.dp)
                        .height(700.dp)
                )
                YearMonthSelector(
                    navigateToGallery = navigateToGallery,
                    apodViewModel = apodViewModel,
                    navigationType = navigationType
                )
            }
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun YearMonthSelector(
    navigateToGallery: () -> Unit,
    apodViewModel: ApodViewModel,
    navigationType: ApodNavigationType) {

    var selectedYear by remember { mutableStateOf(Year.now().value) }
    var selectedMonth by remember { mutableStateOf<String?>(null) }

    val apodUiState by apodViewModel.apodUiState.observeAsState()

    val years = (1996..Year.now().value).toList().reversed()
    val months = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "June",
        "July", "Aug", "Sept", "Oct", "Nov", "Dec"
    )

    Column(modifier = Modifier
        .padding(16.dp),
        verticalArrangement = Arrangement.Center) {
        if (navigationType == ApodNavigationType.BOTTOM_NAVIGATION ||
            navigationType == ApodNavigationType.NAVIGATION_RAIL) {
            Text(
                text = "Please Select the Year That You Want Followed by the Month.",
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 60.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clipToBounds()
                    .padding(vertical = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            ScrollableYearSelector(
                years = years,
                selectedYear = selectedYear,
                onYearSelected = { selectedYear = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.fillMaxWidth()) {
                items(months.size) { index ->
                    val month = months[index]
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00331F)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF98D799)),
                        onClick = {
                            selectedMonth = month
                            val (startDate, endDate) = getDateRangeForMonth(selectedYear, month)
                            apodViewModel.saveSelectedDateRange(startDate, endDate)
                            Log.d("DateUtils", "apodUIState: ${apodUiState?.startDate}, ${apodUiState?.endDate}")
                            navigateToGallery()
                            apodViewModel.getApodsByRange(apodUiState!!.startDate, apodUiState!!.endDate)
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = month)
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.height(80.dp))
            ScrollableYearSelector(
                years = years,
                selectedYear = selectedYear,
                onYearSelected = { selectedYear = it }
            )

            Text(
                text = "Please Select the Year That You Want Followed by the Month.",
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 30.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clipToBounds()
            )
            LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier) {
                items(months.size) { index ->
                    val month = months[index]
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00331F)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF98D799)),
                        onClick = {
                            selectedMonth = month
                            val (startDate, endDate) = getDateRangeForMonth(selectedYear, month)
                            apodViewModel.saveSelectedDateRange(startDate, endDate)
                            Log.d("DateUtils", "apodUIState: ${apodUiState?.startDate}, ${apodUiState?.endDate}")
                            navigateToGallery()
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = month)
                    }
                }
            }
        }
    }
}

@Composable
fun ScrollableYearSelector(
    years: List<Int>,
    selectedYear: Int,
    onYearSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00331F)
            ),
            border = BorderStroke(1.dp, Color(0xFF98D799))) {
            Text(text = "Selected Year: $selectedYear")
        }

        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Row (
                modifier = Modifier,
                verticalAlignment = Alignment.Bottom){
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    containerColor = Color(0xFF004026),
                    shape = RectangleShape,
                    modifier = Modifier
                        .height(400.dp)
                        .padding(10.dp),
                    border = BorderStroke(1.dp, Color.Green)
                ) {
                    years.forEach { year ->
                        DropdownMenuItem(
                            text = { Text(year.toString()) },
                            onClick = {
                                expanded = false
                                onYearSelected(year)
                            }
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("NewApi")
fun getDateRangeForMonth(year: Int, month: String): Pair<String, String> {
    @SuppressLint("NewApi")
    val today = LocalDate.now()
    val formattedDate = DateUtils.formatDate(today)

    val monthIndex = when (month) {
        "Jan" -> 1
        "Feb" -> 2
        "Mar" -> 3
        "Apr" -> 4
        "May" -> 5
        "June" -> 6
        "July" -> 7
        "Aug" -> 8
        "Sept" -> 9
        "Oct" -> 10
        "Nov" -> 11
        "Dec" -> 12
        else -> throw IllegalArgumentException("Invalid month: $month")
    }

    val daysInMonth = when (monthIndex) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (Year.of(year).isLeap) 29 else 28
        else -> throw IllegalArgumentException("Invalid month index: $monthIndex")
    }

    val startDate = "%04d-%02d-01".format(year, monthIndex)
    val endDate = if (today.year == year && today.monthValue == monthIndex) {
        formattedDate
    } else {
        "%04d-%02d-%02d".format(year, monthIndex, daysInMonth)
    }
    return startDate to endDate
}

