package com.app.apiclient.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.apiclient.data.model.ApiKeyLocation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationScreen(
    viewModel: AuthenticationViewModel = viewModel(),
    onAuthConfigChanged: (com.app.apiclient.data.model.AuthConfig) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    
    // Notify parent when auth config changes
    LaunchedEffect(uiState) {
        onAuthConfigChanged(viewModel.getAuthConfig())
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Authentication",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Authentication Type",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
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
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Auth Configuration based on selected type
                when (uiState.authType) {
                    "Basic Auth" -> BasicAuthSection(
                        username = uiState.basicAuthUsername,
                        password = uiState.basicAuthPassword,
                        onUsernameChanged = viewModel::updateBasicAuthUsername,
                        onPasswordChanged = viewModel::updateBasicAuthPassword
                    )
                    "Bearer Token" -> BearerTokenSection(
                        token = uiState.bearerToken,
                        onTokenChanged = viewModel::updateBearerToken
                    )
                    "API Key" -> ApiKeySection(
                        key = uiState.apiKeyKey,
                        value = uiState.apiKeyValue,
                        location = uiState.apiKeyLocation,
                        onKeyChanged = viewModel::updateApiKeyKey,
                        onValueChanged = viewModel::updateApiKeyValue,
                        onLocationChanged = viewModel::updateApiKeyLocation
                    )
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
    }
}

@Composable
private fun BasicAuthSection(
    username: String,
    password: String,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChanged,
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChanged,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
    }
}

@Composable
private fun BearerTokenSection(
    token: String,
    onTokenChanged: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = token,
            onValueChange = onTokenChanged,
            label = { Text("Bearer Token") },
            placeholder = { Text("Enter your bearer token") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "💡 The token will be sent as: Authorization: Bearer <token>",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ApiKeySection(
    key: String,
    value: String,
    location: ApiKeyLocation,
    onKeyChanged: (String) -> Unit,
    onValueChanged: (String) -> Unit,
    onLocationChanged: (ApiKeyLocation) -> Unit
) {
    Column {
        OutlinedTextField(
            value = key,
            onValueChange = onKeyChanged,
            label = { Text("Key Name") },
            placeholder = { Text("e.g., X-API-Key, api_key") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChanged,
            label = { Text("Key Value") },
            placeholder = { Text("Enter your API key") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Location Selection
        var expanded by remember { mutableStateOf(false) }
        val locations = listOf(
            ApiKeyLocation.HEADER to "Header",
            ApiKeyLocation.QUERY_PARAM to "Query Parameter"
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = locations.find { it.first == location }?.second ?: "Header",
                onValueChange = { },
                readOnly = true,
                label = { Text("Location") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                locations.forEach { (loc, name) ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            onLocationChanged(loc)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (location == ApiKeyLocation.HEADER) {
                "💡 The key will be sent as a header: $key: $value"
            } else {
                "💡 The key will be sent as a query parameter: ?$key=$value"
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
