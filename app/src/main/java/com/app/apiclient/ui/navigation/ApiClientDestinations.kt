package com.app.apiclient.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class ApiClientDestinations(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    REQUEST_BUILDER(
        route = "request_builder",
        title = "Request",
        icon = Icons.Default.Send
    ),
    COLLECTIONS(
        route = "collections",
        title = "Collections",
        icon = Icons.Default.List
    ),
    HISTORY(
        route = "history",
        title = "History",
        icon = Icons.Default.DateRange
    ),
    SETTINGS(
        route = "settings",
        title = "Settings",
        icon = Icons.Default.Settings
    );
    
    companion object {
        val bottomNavDestinations = listOf(REQUEST_BUILDER, COLLECTIONS, HISTORY, SETTINGS)
    }
}
