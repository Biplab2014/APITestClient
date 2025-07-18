package com.app.apiclient.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.apiclient.ApiClientApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val application = context.applicationContext as ApiClientApplication
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            application.container.apiRequestRepository,
            application.container.collectionRepository,
            application.container.environmentRepository
        )
    )

    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Error handling
        uiState.error?.let { error ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )

                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("Dismiss")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Statistics Section
        StatisticsSection(uiState = uiState)

        Spacer(modifier = Modifier.height(16.dp))

        // General Settings
        SettingsSection(title = "General") {
            SettingsItem(
                icon = Icons.Default.Settings,
                title = "Dark Mode",
                subtitle = "Switch between light and dark themes",
                trailing = {
                    Switch(
                        checked = uiState.isDarkMode,
                        onCheckedChange = { viewModel.toggleDarkMode() }
                    )
                }
            )

            SettingsItem(
                icon = Icons.Default.Settings,
                title = "Auto-save Requests",
                subtitle = "Automatically save requests to history",
                trailing = {
                    Switch(
                        checked = uiState.autoSaveRequests,
                        onCheckedChange = { viewModel.toggleAutoSave() }
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Network Settings
        SettingsSection(title = "Network") {
            SettingsItem(
                icon = Icons.Default.Settings,
                title = "Request Timeout",
                subtitle = "${uiState.requestTimeout} seconds",
                onClick = { viewModel.showTimeoutDialog() }
            )

            SettingsItem(
                icon = Icons.Default.Settings,
                title = "SSL Verification",
                subtitle = "Verify SSL certificates",
                trailing = {
                    Switch(
                        checked = uiState.sslVerification,
                        onCheckedChange = { viewModel.toggleSslVerification() }
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Data Settings
        SettingsSection(title = "Data") {
            SettingsItem(
                icon = Icons.Default.Share,
                title = "Export Data",
                subtitle = "Export collections and history",
                onClick = { viewModel.showExportDialog() }
            )

            SettingsItem(
                icon = Icons.Default.Add,
                title = "Import Data",
                subtitle = "Import from Postman or other tools",
                onClick = { viewModel.showImportDialog() }
            )

            SettingsItem(
                icon = Icons.Default.Delete,
                title = "Clear History",
                subtitle = "Remove all request history",
                onClick = { viewModel.showClearHistoryDialog() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // About Section
        SettingsSection(title = "About") {
            SettingsItem(
                icon = Icons.Default.Info,
                title = "Version",
                subtitle = "1.0.0 (Beta)"
            )

            SettingsItem(
                icon = Icons.Default.Info,
                title = "Report Bug",
                subtitle = "Help us improve the app",
                onClick = { /* TODO: Bug report */ }
            )

            SettingsItem(
                icon = Icons.Default.Star,
                title = "Rate App",
                subtitle = "Rate us on the Play Store",
                onClick = { /* TODO: Rate app */ }
            )
        }

        // Dialogs
        if (uiState.showTimeoutDialog) {
            TimeoutDialog(
                currentTimeout = uiState.requestTimeout,
                onDismiss = { viewModel.hideTimeoutDialog() },
                onConfirm = { timeout -> viewModel.updateTimeout(timeout) }
            )
        }

        if (uiState.showClearHistoryDialog) {
            ClearHistoryDialog(
                onDismiss = { viewModel.hideClearHistoryDialog() },
                onConfirm = { viewModel.clearHistory() }
            )
        }

        if (uiState.showExportDialog) {
            ExportDialog(
                isLoading = uiState.isLoading,
                onDismiss = { viewModel.hideExportDialog() },
                onConfirm = { viewModel.exportData() }
            )
        }

        if (uiState.showImportDialog) {
            ImportDialog(
                isLoading = uiState.isLoading,
                onDismiss = { viewModel.hideImportDialog() },
                onConfirm = { viewModel.importData() }
            )
        }
    }
}

@Composable
private fun StatisticsSection(uiState: SettingsUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    title = "Requests",
                    value = uiState.totalRequests.toString(),
                    icon = Icons.Default.Send
                )

                StatisticItem(
                    title = "Collections",
                    value = uiState.totalCollections.toString(),
                    icon = Icons.Default.List
                )

                StatisticItem(
                    title = "Environments",
                    value = uiState.totalEnvironments.toString(),
                    icon = Icons.Default.Settings
                )
            }
        }
    }
}

@Composable
private fun StatisticItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val modifier = if (onClick != null) {
        Modifier.fillMaxWidth()
    } else {
        Modifier.fillMaxWidth()
    }

    Surface(
        onClick = onClick ?: {},
        modifier = modifier,
        enabled = onClick != null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            trailing?.invoke()
        }
    }
}

@Composable
private fun TimeoutDialog(
    currentTimeout: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var timeout by remember { mutableStateOf(currentTimeout.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Request Timeout") },
        text = {
            Column {
                Text(
                    text = "Set the request timeout in seconds",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = timeout,
                    onValueChange = { timeout = it },
                    label = { Text("Timeout (seconds)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    timeout.toIntOrNull()?.let { timeoutValue ->
                        if (timeoutValue > 0) {
                            onConfirm(timeoutValue)
                        }
                    }
                },
                enabled = timeout.toIntOrNull()?.let { it > 0 } == true
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ClearHistoryDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Clear History") },
        text = {
            Text(
                text = "Are you sure you want to clear all request history? This action cannot be undone.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Clear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ExportDialog(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = if (isLoading) { {} } else onDismiss,
        title = { Text("Export Data") },
        text = {
            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Exporting data...")
                }
            } else {
                Text(
                    text = "Export all collections, requests, and environments to a JSON file.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            if (!isLoading) {
                TextButton(onClick = onConfirm) {
                    Text("Export")
                }
            }
        },
        dismissButton = {
            if (!isLoading) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

@Composable
private fun ImportDialog(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = if (isLoading) { {} } else onDismiss,
        title = { Text("Import Data") },
        text = {
            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Importing data...")
                }
            } else {
                Column {
                    Text(
                        text = "Import collections and requests from:",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "• Postman collections\n• Insomnia workspaces\n• OpenAPI/Swagger files\n• JSON exports",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            if (!isLoading) {
                TextButton(onClick = onConfirm) {
                    Text("Import")
                }
            }
        },
        dismissButton = {
            if (!isLoading) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
