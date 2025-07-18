package com.app.apiclient.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.apiclient.ui.screens.collections.CollectionsScreen
import com.app.apiclient.ui.screens.history.HistoryScreen
import com.app.apiclient.ui.screens.request.RequestBuilderScreen
import com.app.apiclient.ui.screens.settings.SettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiClientNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                ApiClientDestinations.bottomNavDestinations.forEach { destination ->
                    NavigationBarItem(
                        icon = { Icon(destination.icon, contentDescription = destination.title) },
                        label = { Text(destination.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true,
                        onClick = {
                            navController.navigate(destination.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ApiClientDestinations.REQUEST_BUILDER.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(ApiClientDestinations.REQUEST_BUILDER.route) {
                RequestBuilderScreen()
            }
            composable(ApiClientDestinations.COLLECTIONS.route) {
                CollectionsScreen()
            }
            composable(ApiClientDestinations.HISTORY.route) {
                HistoryScreen()
            }
            composable(ApiClientDestinations.SETTINGS.route) {
                SettingsScreen()
            }
        }
    }
}
