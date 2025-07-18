package com.app.apiclient.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.apiclient.data.model.ApiResponse
import org.json.JSONObject
import org.json.JSONArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResponseViewer(
    response: ApiResponse,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Pretty", "Raw", "Headers")
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Response Status and Metrics
            ResponseStatusBar(response = response)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tab Row
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tab Content
            when (selectedTab) {
                0 -> PrettyJsonTab(response.body)
                1 -> RawTab(response.body)
                2 -> HeadersTab(response.headers)
            }
        }
    }
}

@Composable
private fun ResponseStatusBar(response: ApiResponse) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = getStatusColor(response.statusCode),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "${response.statusCode} ${response.statusMessage}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            if (response.isError && !response.errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "Error",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "${response.responseTime}ms",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatBytes(response.responseSize),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PrettyJsonTab(body: String?) {
    val clipboardManager = LocalClipboardManager.current
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Response Body",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            
            if (!body.isNullOrBlank()) {
                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(body))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Copy Response"
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (body.isNullOrBlank()) {
            Text(
                text = "No response body",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            val formattedJson = formatJson(body)
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                val scrollState = rememberScrollState()
                val horizontalScrollState = rememberScrollState()
                
                Text(
                    text = formattedJson,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                        .height(300.dp) // Fixed height to prevent infinite constraints
                        .verticalScroll(scrollState)
                        .horizontalScroll(horizontalScrollState)
                )
            }
        }
    }
}

@Composable
private fun RawTab(body: String?) {
    val clipboardManager = LocalClipboardManager.current
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Raw Response",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            
            if (!body.isNullOrBlank()) {
                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(body))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Copy Response"
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (body.isNullOrBlank()) {
            Text(
                text = "No response body",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                val scrollState = rememberScrollState()
                val horizontalScrollState = rememberScrollState()
                
                SelectionContainer {
                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth()
                            .height(300.dp) // Fixed height to prevent infinite constraints
                            .verticalScroll(scrollState)
                            .horizontalScroll(horizontalScrollState)
                    )
                }
            }
        }
    }
}

@Composable
private fun HeadersTab(headers: Map<String, String>) {
    val clipboardManager = LocalClipboardManager.current
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Response Headers",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            
            if (headers.isNotEmpty()) {
                IconButton(
                    onClick = {
                        val headersText = headers.entries.joinToString("\n") { "${it.key}: ${it.value}" }
                        clipboardManager.setText(AnnotatedString(headersText))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Copy Headers"
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (headers.isEmpty()) {
            Text(
                text = "No response headers",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    headers.forEach { (key, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "$key: ",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.weight(0.3f)
                            )
                            Text(
                                text = value,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.weight(0.7f)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
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

private fun formatBytes(bytes: Long): String {
    return when {
        bytes < 1024 -> "${bytes}B"
        bytes < 1024 * 1024 -> "${bytes / 1024}KB"
        else -> "${bytes / (1024 * 1024)}MB"
    }
}

private fun formatJson(jsonString: String): String {
    return try {
        when {
            jsonString.trim().startsWith("{") -> {
                val jsonObject = JSONObject(jsonString)
                jsonObject.toString(2)
            }
            jsonString.trim().startsWith("[") -> {
                val jsonArray = JSONArray(jsonString)
                jsonArray.toString(2)
            }
            else -> jsonString
        }
    } catch (e: Exception) {
        jsonString // Return original if not valid JSON
    }
}
