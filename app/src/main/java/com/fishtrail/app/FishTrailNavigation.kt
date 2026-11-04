package com.fishtrail.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fishtrail.app.auth.LoginScreen
import com.fishtrail.app.auth.RegisterScreen
import com.fishtrail.app.catchlog.CatchLogScreen
import com.fishtrail.app.data.FishTrailDatabase
import com.fishtrail.app.history.HistoryScreen
import com.fishtrail.app.home.HomeScreen
import com.fishtrail.app.map.MapScreen
import com.fishtrail.app.settings.AppPreferences
import com.fishtrail.app.settings.SettingsScreen
import com.fishtrail.app.viewmodel.AuthViewModel
import com.fishtrail.app.viewmodel.AuthViewModelFactory
import com.fishtrail.app.viewmodel.CatchViewModel
import com.fishtrail.app.viewmodel.CatchViewModelFactory

@Composable
fun FishTrailNavigation(
    darkModeEnabled: Boolean,
    onDarkModeChanged: (Boolean) -> Unit
) {

    val navController = rememberNavController()
    val context = LocalContext.current

    var isDarkModeEnabled by remember {
        mutableStateOf(
            AppPreferences.isDarkModeEnabled(context)
        )
    }

    val database = remember {
        FishTrailDatabase.getDatabase(context)
    }

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory()
    )

    val catchViewModel: CatchViewModel = viewModel(
        factory = CatchViewModelFactory(database.catchDao())
    )

    var loggedInUsername by remember {
        mutableStateOf("")
    }

    var selectedLatitude by remember {
        mutableStateOf<Double?>(null)
    }

    var selectedLongitude by remember {
        mutableStateOf<Double?>(null)
    }

    var selectedLocationName by remember {
        mutableStateOf("")
    }

    var selectingLocationForCatch by remember {
        mutableStateOf(false)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        "home",
        "catchlog",
        "map",
        "history",
        "settings"
    )

    val bottomBarRoute = currentRoute ?: "home"

    Scaffold(
        bottomBar = {

            if (showBottomBar) {

                BottomNavigationBar(
                    currentRoute = bottomBarRoute,
                    onItemClick = { route ->

                        navController.navigate(route) {

                            popUpTo("home") {
                                saveState = true
                            }

                            launchSingleTop = true

                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            NavHost(
                navController = navController,
                startDestination = "login"
            ) {

                composable("login") {

                    LoginScreen(
                        authViewModel = authViewModel,

                        onLoginSuccess = { username ->

                            loggedInUsername = username

                            navController.navigate("home") {

                                popUpTo("login") {
                                    inclusive = true
                                }
                            }
                        },

                        onRegisterClick = {
                            navController.navigate("register")
                        }
                    )
                }

                composable("register") {

                    RegisterScreen(
                        authViewModel = authViewModel,

                        onLoginClick = {
                            navController.popBackStack()
                        }
                    )
                }

                composable("home") {

                    HomeScreen(
                        catchViewModel = catchViewModel,
                        authViewModel = authViewModel,
                        username = loggedInUsername,
                        onLogCatchClick = {
                            navController.navigate("catchlog")
                        },
                        onOpenMapClick = {
                            navController.navigate("map")
                        },

                        onLogoutClick = {
                            authViewModel.logout()
                            loggedInUsername = ""

                            navController.navigate("login") {
                                popUpTo("home") {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }

                composable("catchlog") {

                    CatchLogScreen(
                        catchViewModel = catchViewModel,
                        username = loggedInUsername,

                        selectedLatitude = selectedLatitude,
                        selectedLongitude = selectedLongitude,
                        selectedLocationName = selectedLocationName,

                        onSelectLocationOnMap = {

                            selectingLocationForCatch = true

                            navController.navigate("map")
                        },

                        onLocationUsed = {

                            selectedLatitude = null
                            selectedLongitude = null
                            selectedLocationName = ""
                        }
                    )
                }

                composable("map") {

                    MapScreen(
                        catchViewModel = catchViewModel,
                        username = loggedInUsername,

                        selectingLocationForCatch =
                            selectingLocationForCatch,

                        onLocationSelected = {
                                latitude,
                                longitude,
                                locationName ->

                            selectedLatitude = latitude
                            selectedLongitude = longitude
                            selectedLocationName = locationName

                            selectingLocationForCatch = false

                            navController.popBackStack()
                        }
                    )
                }

                composable("history") {

                    HistoryScreen(
                        catchViewModel = catchViewModel,
                        username = loggedInUsername
                    )
                }

                composable("settings") {

                    SettingsScreen(
                        authViewModel = authViewModel,
                        username = loggedInUsername,
                        darkModeEnabled = darkModeEnabled,

                        onDarkModeChanged = { enabled ->

                            onDarkModeChanged(enabled)
                        },

                        onLogoutClick = {
                            authViewModel.logout()
                            loggedInUsername = ""

                            navController.navigate("login") {
                                popUpTo("home") {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
