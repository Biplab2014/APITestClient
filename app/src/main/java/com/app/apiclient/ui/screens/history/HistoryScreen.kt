package com.app.apiclient.ui.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

data class HistoryItem(
    val method: String,
    val url: String,
    val statusCode: Int,
    val timestamp: Long,
    val responseTime: Long
)

@Composable
fun HistoryScreen() {
    // Mock history data
    val historyItems = remember {
        listOf(
            HistoryItem("GET", "https://jsonplaceholder.typicode.com/posts/1", 200, System.currentTimeMillis() - 3600000, 245),
            HistoryItem("POST", "https://api.example.com/users", 201, System.currentTimeMillis() - 7200000, 567),
            HistoryItem("GET", "https://api.github.com/user", 200, System.currentTimeMillis() - 10800000, 123),
            HistoryItem("PUT", "https://api.example.com/posts/1", 200, System.currentTimeMillis() - 14400000, 432),
            HistoryItem("DELETE", "https://api.example.com/posts/1", 404, System.currentTimeMillis() - 18000000, 234),
            HistoryItem("GET", "https://httpbin.org/get", 200, System.currentTimeMillis() - 21600000, 345)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "History",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (historyItems.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "No requests yet",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Your request history will appear here",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(historyItems) { item ->
                    HistoryItemCard(item = item)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryItemCard(item: HistoryItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // HTTP Method Badge
                    Surface(
                        color = getMethodColor(item.method),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = item.method,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Status Code Badge
                    Surface(
                        color = getStatusColor(item.statusCode),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = item.statusCode.toString(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "${item.responseTime}ms",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.url,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formatTimestamp(item.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun getMethodColor(method: String): Color {
    return when (method) {
        "GET" -> Color(0xFF4CAF50)
        "POST" -> Color(0xFF2196F3)
        "PUT" -> Color(0xFFFF9800)
        "DELETE" -> Color(0xFFF44336)
        "PATCH" -> Color(0xFF9C27B0)
        else -> Color(0xFF607D8B)
    }
}

@Composable
private fun getStatusColor(statusCode: Int): Color {
    return when (statusCode) {
        in 200..299 -> Color(0xFF4CAF50)
        in 300..399 -> Color(0xFFFF9800)
        in 400..499 -> Color(0xFFF44336)
        in 500..599 -> Color(0xFF9C27B0)
        else -> Color(0xFF607D8B)
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
