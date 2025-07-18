package com.app.apiclient.ui.screens.request

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import com.app.apiclient.ApiClientApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestBuilderScreen() {
    val context = LocalContext.current
    val application = context.applicationContext as ApiClientApplication
    val viewModel: RequestBuilderViewModel = viewModel(
        factory = RequestBuilderViewModelFactory(
            application.container.apiRequestRepository,
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
        // HTTP Method and URL Section
        HttpMethodAndUrlSection(
            selectedMethod = uiState.selectedMethod,
            url = uiState.url,
            onMethodSelected = viewModel::updateMethod,
            onUrlChanged = viewModel::updateUrl
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Query Parameters Section
        QueryParametersSection(
            queryParams = uiState.queryParams,
            onAddParam = viewModel::addQueryParam,
            onUpdateParam = viewModel::updateQueryParam,
            onRemoveParam = viewModel::removeQueryParam
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Headers Section
        HeadersSection(
            headers = uiState.headers,
            onAddHeader = viewModel::addHeader,
            onUpdateHeader = viewModel::updateHeader,
            onRemoveHeader = viewModel::removeHeader
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Request Body Section
        RequestBodySection(
            bodyType = uiState.bodyType,
            body = uiState.body,
            onBodyTypeChanged = viewModel::updateBodyType,
            onBodyChanged = viewModel::updateBody
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Authentication Section
        AuthenticationSection(
            authConfig = uiState.authConfig,
            onAuthConfigChanged = viewModel::updateAuthConfig
        )

        Spacer(modifier = Modifier.height(16.dp))

        // cURL Section
        val clipboardManager = LocalClipboardManager.current
        CurlSection(
            onImportCurl = viewModel::showCurlDialog,
            onExportCurl = {
                val curlCommand = viewModel.generateCurlCommand()
                clipboardManager.setText(AnnotatedString(curlCommand))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sample Requests Section
        SampleRequestsSection(
            onSampleSelected = viewModel::loadSampleRequest
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Send Request Button
        Button(
            onClick = { viewModel.sendRequest() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = uiState.url.isNotBlank() && !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sending...")
            } else {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send Request"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send Request")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Response Section
        uiState.response?.let { response ->
            com.app.apiclient.ui.components.ResponseViewer(
                response = response,
                modifier = Modifier.fillMaxWidth()
            )
        }

        uiState.error?.let { error ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Error: $error",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // cURL Import Dialog
        if (uiState.showCurlDialog) {
            CurlImportDialog(
                curlCommand = uiState.curlCommand,
                onCurlCommandChanged = viewModel::updateCurlCommand,
                onDismiss = viewModel::hideCurlDialog,
                onImport = viewModel::importFromCurl
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HttpMethodAndUrlSection(
    selectedMethod: String,
    url: String,
    onMethodSelected: (String) -> Unit,
    onUrlChanged: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Request",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // HTTP Method Dropdown
                var expanded by remember { mutableStateOf(false) }
                val methods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS")

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.width(120.dp)
                ) {
                    OutlinedTextField(
                        value = selectedMethod,
                        onValueChange = { },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        methods.forEach { method ->
                            DropdownMenuItem(
                                text = { Text(method) },
                                onClick = {
                                    onMethodSelected(method)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // URL Input
                OutlinedTextField(
                    value = url,
                    onValueChange = onUrlChanged,
                    label = { Text("URL") },
                    placeholder = { Text("https://api.example.com/endpoint") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }
    }
}

@Composable
private fun QueryParametersSection(
    queryParams: List<Pair<String, String>>,
    onAddParam: () -> Unit,
    onUpdateParam: (Int, String, String) -> Unit,
    onRemoveParam: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Query Parameters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                TextButton(onClick = onAddParam) {
                    Text("+ Add")
                }
            }

            if (queryParams.isEmpty()) {
                Text(
                    text = "No query parameters",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                queryParams.forEachIndexed { index, (key, value) ->
                    KeyValueRow(
                        key = key,
                        value = value,
                        onKeyChanged = { newKey -> onUpdateParam(index, newKey, value) },
                        onValueChanged = { newValue -> onUpdateParam(index, key, newValue) },
                        onRemove = { onRemoveParam(index) }
                    )

                    if (index < queryParams.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun HeadersSection(
    headers: List<Pair<String, String>>,
    onAddHeader: () -> Unit,
    onUpdateHeader: (Int, String, String) -> Unit,
    onRemoveHeader: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Headers",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row {
                    TextButton(onClick = onAddHeader) {
                        Text("+ Add")
                    }

                    var showPresets by remember { mutableStateOf(false) }
                    TextButton(onClick = { showPresets = true }) {
                        Text("Presets")
                    }

                    if (showPresets) {
                        HeaderPresetsDialog(
                            onDismiss = { showPresets = false },
                            onHeaderSelected = { key, value ->
                                onAddHeader()
                                // Get the last index and update it
                                val lastIndex = headers.size
                                onUpdateHeader(lastIndex, key, value)
                                showPresets = false
                            }
                        )
                    }
                }
            }

            if (headers.isEmpty()) {
                Text(
                    text = "No headers",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                headers.forEachIndexed { index, (key, value) ->
                    KeyValueRow(
                        key = key,
                        value = value,
                        onKeyChanged = { newKey -> onUpdateHeader(index, newKey, value) },
                        onValueChanged = { newValue -> onUpdateHeader(index, key, newValue) },
                        onRemove = { onRemoveHeader(index) }
                    )

                    if (index < headers.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KeyValueRow(
    key: String,
    value: String,
    onKeyChanged: (String) -> Unit,
    onValueChanged: (String) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = key,
            onValueChange = onKeyChanged,
            label = { Text("Key") },
            modifier = Modifier.weight(1f),
            singleLine = true
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChanged,
            label = { Text("Value") },
            modifier = Modifier.weight(1f),
            singleLine = true
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RequestBodySection(
    bodyType: String,
    body: String,
    onBodyTypeChanged: (String) -> Unit,
    onBodyChanged: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Request Body",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Body Type Selection
            var expanded by remember { mutableStateOf(false) }
            val bodyTypes = listOf("None", "Raw JSON", "Form Data", "URL Encoded")

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = bodyType,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Body Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    bodyTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                onBodyTypeChanged(type)
                                expanded = false
                            }
                        )
                    }
                }
            }

            if (bodyType != "None") {
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = body,
                    onValueChange = onBodyChanged,
                    label = { Text("Request Body") },
                    placeholder = {
                        Text(
                            when (bodyType) {
                                "Raw JSON" -> "{\n  \"key\": \"value\"\n}"
                                "Form Data" -> "key1=value1&key2=value2"
                                "URL Encoded" -> "key1=value1&key2=value2"
                                else -> "Enter request body"
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 6
                )
            }
        }
    }
}





@Composable
private fun SampleRequestsSection(
    onSampleSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Sample Requests",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onSampleSelected("GET_POSTS") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Get Posts", style = MaterialTheme.typography.bodySmall)
                }

                OutlinedButton(
                    onClick = { onSampleSelected("GET_USER") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Get User", style = MaterialTheme.typography.bodySmall)
                }

                OutlinedButton(
                    onClick = { onSampleSelected("POST_USER") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Create User", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "💡 These samples use environment variables like {{baseUrl}} and {{userId}}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

@Composable
private fun AuthenticationSection(
    authConfig: com.app.apiclient.data.model.AuthConfig,
    onAuthConfigChanged: (com.app.apiclient.data.model.AuthConfig) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Authentication",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                TextButton(onClick = { expanded = !expanded }) {
                    Text(if (expanded) "Hide" else "Configure")
                }
            }

            // Show current auth type
            Text(
                text = when (authConfig) {
                    is com.app.apiclient.data.model.AuthConfig.None -> "No authentication"
                    is com.app.apiclient.data.model.AuthConfig.BasicAuth -> "Basic Auth (${authConfig.username})"
                    is com.app.apiclient.data.model.AuthConfig.BearerToken -> "Bearer Token"
                    is com.app.apiclient.data.model.AuthConfig.ApiKey -> "API Key (${authConfig.key})"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))

                // Use a simplified auth configuration instead of the full screen
                AuthenticationConfigSection(
                    onAuthConfigChanged = onAuthConfigChanged
                )
            }
        }
    }
}

@Composable
private fun HeaderPresetsDialog(
    onDismiss: () -> Unit,
    onHeaderSelected: (String, String) -> Unit
) {
    val commonHeaders = listOf(
        "Content-Type" to "application/json",
        "Content-Type" to "application/x-www-form-urlencoded",
        "Content-Type" to "multipart/form-data",
        "Accept" to "application/json",
        "Accept" to "*/*",
        "Authorization" to "Bearer ",
        "User-Agent" to "ApiClient/1.0",
        "Cache-Control" to "no-cache",
        "X-Requested-With" to "XMLHttpRequest"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Common Headers") },
        text = {
            LazyColumn(
                modifier = Modifier.height(300.dp) // Fixed height to prevent infinite constraints
            ) {
                items(commonHeaders) { (key, value) ->
                    TextButton(
                        onClick = { onHeaderSelected(key, value) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = key,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = value,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    HorizontalDivider()
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun CurlSection(
    onImportCurl: () -> Unit,
    onExportCurl: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "cURL Commands",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onImportCurl,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Import cURL",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Import cURL", style = MaterialTheme.typography.bodySmall)
                }

                OutlinedButton(
                    onClick = onExportCurl,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Export cURL",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export cURL", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "💡 Import cURL commands from other tools or export your request as cURL",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

@Composable
private fun CurlImportDialog(
    curlCommand: String,
    onCurlCommandChanged: (String) -> Unit,
    onDismiss: () -> Unit,
    onImport: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Import cURL Command") },
        text = {
            Column {
                Text(
                    text = "Paste your cURL command below:",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = curlCommand,
                    onValueChange = onCurlCommandChanged,
                    label = { Text("cURL Command") },
                    placeholder = {
                        Text("curl -X GET \"https://api.example.com/users\" -H \"Authorization: Bearer token\"")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 6
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "💡 Supports most cURL options including -X, -H, -d, --data, etc.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onImport,
                enabled = curlCommand.isNotBlank()
            ) {
                Text("Import")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthenticationConfigSection(
    onAuthConfigChanged: (com.app.apiclient.data.model.AuthConfig) -> Unit
) {
    val viewModel: com.app.apiclient.ui.screens.auth.AuthenticationViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    // Notify parent when auth config changes
    LaunchedEffect(uiState) {
        onAuthConfigChanged(viewModel.getAuthConfig())
    }

    Column {
        Text(
            text = "Authentication Type",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Auth Type Selection
        var expanded by remember { mutableStateOf(false) }
        val authTypes = listOf("None", "Basic Auth", "Bearer Token", "API Key")

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = uiState.authType,
                onValueChange = { },
                readOnly = true,
                label = { Text("Authentication Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                authTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            viewModel.updateAuthType(type)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Simplified auth configuration
        when (uiState.authType) {
            "Basic Auth" -> {
                OutlinedTextField(
                    value = uiState.basicAuthUsername,
                    onValueChange = viewModel::updateBasicAuthUsername,
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.basicAuthPassword,
                    onValueChange = viewModel::updateBasicAuthPassword,
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                )
            }
            "Bearer Token" -> {
                OutlinedTextField(
                    value = uiState.bearerToken,
                    onValueChange = viewModel::updateBearerToken,
                    label = { Text("Bearer Token") },
                    placeholder = { Text("Enter your bearer token") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            "None" -> {
                Text(
                    text = "No authentication will be used for this request.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
