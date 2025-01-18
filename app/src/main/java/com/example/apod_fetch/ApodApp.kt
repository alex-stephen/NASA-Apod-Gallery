package com.example.apod_fetch

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.apod_fetch.data.ApodDatabase
import com.example.apod_fetch.data.ApodRepository
import com.example.apod_fetch.navigation.ApodNavHost
import com.example.apod_fetch.ui.ApodViewModel
import com.example.apod_fetch.ui.ApodViewModelFactory
import com.example.apod_fetch.ui.screens.FavouritePhotosDestination
import com.example.apod_fetch.ui.screens.GalleryDestination
import com.example.apod_fetch.ui.screens.GallerySelectDestination
import com.example.apod_fetch.ui.screens.HomeDestination
import com.example.apod_fetch.ui.screens.RandomPhotosDestination
import com.example.apod_fetch.ui.theme.DarkColorScheme
import com.example.apod_fetch.utils.ApodNavigationType

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ApodApp(
    windowSize: WindowWidthSizeClass,
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val navigationType = when (windowSize) {
        WindowWidthSizeClass.Compact -> ApodNavigationType.BOTTOM_NAVIGATION
        WindowWidthSizeClass.Medium -> ApodNavigationType.NAVIGATION_RAIL
        WindowWidthSizeClass.Expanded -> ApodNavigationType.PERMANENT_NAVIGATION_DRAWER
        else -> ApodNavigationType.BOTTOM_NAVIGATION
    }

    val context = LocalContext.current
    val database = ApodDatabase.getDatabase(context)
    val apodDao = database.apodDao()
    val apodRepository = ApodRepository(apodDao)
    val apodViewModelFactory = ApodViewModelFactory(apodRepository)
    val apodViewModel: ApodViewModel = viewModel(factory = apodViewModelFactory)

    Log.d("ApodApp", "NavigationType: $navigationType")


    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (navigationType == ApodNavigationType.BOTTOM_NAVIGATION) {
                ApodBottomNavBar(
                    navController = navController,
                    currentRoute = currentRoute ?: ""
                )
            }
        }
    ) {
        if (navigationType == ApodNavigationType.PERMANENT_NAVIGATION_DRAWER) {
            PermanentNavigationDrawer(
                drawerContent = {
                    PermanentDrawerSheet(
                        drawerContainerColor = Color(0xFF00331F),
                        drawerContentColor = Color(0xFFD9FFD2),
                        modifier = Modifier.width(150.dp)) {
                        PermanentDrawerContent(
                            navController = navController,
                            currentRoute = currentRoute
                        )
                    }
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.inverseOnSurface)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        ApodNavHost(
                            navController = navController,
                            apodViewModel = apodViewModel,
                            modifier = Modifier.fillMaxSize(),
                            apodViewModelFactory = apodViewModelFactory,
                            navigationType = navigationType
                        )
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.inverseOnSurface)
            ) {
                if (navigationType == ApodNavigationType.NAVIGATION_RAIL) {
                    ApodRailNavBar(
                        navController = navController,
                        currentRoute = currentRoute ?: ""
                    )
                }
                Column(modifier = Modifier.fillMaxSize()) {
                    ApodNavHost(
                        navController = navController,
                        apodViewModel = apodViewModel,
                        modifier = Modifier.fillMaxSize(),
                        apodViewModelFactory = apodViewModelFactory,
                        navigationType = navigationType
                    )
                }
            }
        }
    }
}


/**
 * App bar to display title and conditionally display the back navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApodTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {}
) {
    CenterAlignedTopAppBar(title = { Text(title) },
        modifier = modifier,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(Color(0xFF00331F)),
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        tint = Color(0xFF98D799),
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        })
}

@Composable
fun ApodBottomNavBar(
    navController: NavHostController,
    currentRoute: String
) {
    NavigationBar (containerColor = Color(0xFF00331F)) {

        val destinations = listOf(
            HomeDestination to Icons.Default.Home,
            GallerySelectDestination to Icons.Default.ImageSearch,
            GalleryDestination to Icons.Default.PhotoLibrary,
            RandomPhotosDestination to Icons.Default.Casino,
            FavouritePhotosDestination to Icons.Default.Favorite
        )

        destinations.forEach { (destination, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = null) },
                label = { Text(text = destination.route.uppercase()) },
                selected = currentRoute == destination.route,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF99D99A),
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color(0xFF517E5B)
                ),
                onClick = {
                    if (currentRoute != destination.route) {
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}


@Composable
fun ApodRailNavBar(navController: NavHostController, currentRoute: String) {
    NavigationRail (containerColor = Color(0xFF00331F)){
        val destinations = listOf(
            HomeDestination to Icons.Default.Home,
            GallerySelectDestination to Icons.Default.ImageSearch,
            GalleryDestination to Icons.Default.PhotoLibrary,
            RandomPhotosDestination to Icons.Default.Casino,
            FavouritePhotosDestination to Icons.Default.Favorite
        )

        destinations.forEach { (destination, icon) ->
            NavigationRailItem(
                icon = { Icon(icon, contentDescription = null) },
                label = { Text(destination.route.uppercase())},
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = Color(0xFF99D99A),
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color(0xFF517E5B)
                ),
                selected = currentRoute == destination.route,
                onClick = {
                    if (currentRoute != destination.route) {
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun PermanentDrawerContent(navController: NavHostController, currentRoute: String?) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        Text(
            text = "Navigation",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Example items in the permanent drawer
        DrawerItem(
            label = HomeDestination.route.uppercase(),
            isSelected = currentRoute == HomeDestination.route
        ) {
            navController.navigate(HomeDestination.route) {
                popUpTo(navController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }

        DrawerItem(
            label = GallerySelectDestination.route.uppercase(),
            isSelected = currentRoute == GallerySelectDestination.route
        ) {
            navController.navigate(GallerySelectDestination.route) {
                popUpTo(navController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }

        DrawerItem(
            label = GalleryDestination.route.uppercase(),
            isSelected = currentRoute == GalleryDestination.route
        ) {
            navController.navigate(GalleryDestination.route) {
                popUpTo(navController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }

        DrawerItem(
            label = RandomPhotosDestination.route.uppercase(),
            isSelected = currentRoute == RandomPhotosDestination.route
        ) {
            navController.navigate(RandomPhotosDestination.route) {
                popUpTo(navController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }

        DrawerItem(
            label = FavouritePhotosDestination.route.uppercase(),
            isSelected = currentRoute == FavouritePhotosDestination.route
        ) {
            navController.navigate(FavouritePhotosDestination.route) {
                popUpTo(navController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
}

@Composable
fun DrawerItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val textColor = if (isSelected) {
        DarkColorScheme.tertiary
    } else {
        DarkColorScheme.secondary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = textColor)
    }
}


