package com.app.apiclient.utils

import com.app.apiclient.data.model.HttpMethod

data class ParsedCurlCommand(
    val url: String,
    val method: HttpMethod = HttpMethod.GET,
    val headers: Map<String, String> = emptyMap(),
    val body: String? = null,
    val queryParams: Map<String, String> = emptyMap()
)

object CurlParser {
    
    fun parseCurl(curlCommand: String): ParsedCurlCommand? {
        try {
            val trimmed = curlCommand.trim()
            if (!trimmed.startsWith("curl")) {
                return null
            }
            
            // Remove 'curl' and split by spaces, but preserve quoted strings
            val parts = splitCurlCommand(trimmed.substring(4).trim())
            
            var url = ""
            var method = HttpMethod.GET
            val headers = mutableMapOf<String, String>()
            var body: String? = null
            val queryParams = mutableMapOf<String, String>()
            
            var i = 0
            while (i < parts.size) {
                val part = parts[i]
                
                when {
                    part == "-X" || part == "--request" -> {
                        if (i + 1 < parts.size) {
                            method = parseHttpMethod(parts[i + 1])
                            i += 2
                        } else {
                            i++
                        }
                    }
                    part == "-H" || part == "--header" -> {
                        if (i + 1 < parts.size) {
                            val header = parts[i + 1]
                            parseHeader(header)?.let { (key, value) ->
                                headers[key] = value
                            }
                            i += 2
                        } else {
                            i++
                        }
                    }
                    part == "-d" || part == "--data" || part == "--data-raw" -> {
                        if (i + 1 < parts.size) {
                            body = parts[i + 1]
                            if (method == HttpMethod.GET) {
                                method = HttpMethod.POST
                            }
                            i += 2
                        } else {
                            i++
                        }
                    }
                    part == "--data-urlencode" -> {
                        if (i + 1 < parts.size) {
                            // For URL encoded data, we'll treat it as body
                            body = parts[i + 1]
                            if (method == HttpMethod.GET) {
                                method = HttpMethod.POST
                            }
                            i += 2
                        } else {
                            i++
                        }
                    }
                    part.startsWith("http://") || part.startsWith("https://") -> {
                        url = part
                        // Extract query parameters from URL
                        val urlParts = url.split("?", limit = 2)
                        if (urlParts.size == 2) {
                            url = urlParts[0]
                            parseQueryParams(urlParts[1]).forEach { (key, value) ->
                                queryParams[key] = value
                            }
                        }
                        i++
                    }
                    else -> {
                        // Skip unknown options
                        i++
                    }
                }
            }
            
            if (url.isEmpty()) {
                return null
            }
            
            return ParsedCurlCommand(
                url = url,
                method = method,
                headers = headers,
                body = body,
                queryParams = queryParams
            )
            
        } catch (e: Exception) {
            return null
        }
    }
    
    private fun splitCurlCommand(command: String): List<String> {
        val parts = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false
        var quoteChar = ' '
        var i = 0

        // First, clean up the command by removing extra whitespace
        val cleanCommand = command.replace(Regex("\\s+"), " ").trim()

        while (i < cleanCommand.length) {
            val char = cleanCommand[i]

            when {
                !inQuotes && (char == '"' || char == '\'') -> {
                    inQuotes = true
                    quoteChar = char
                    // Don't include the quote in the result
                }
                inQuotes && char == quoteChar -> {
                    inQuotes = false
                    // Don't include the quote in the result
                }
                !inQuotes && char == ' ' -> {
                    if (current.isNotEmpty()) {
                        parts.add(current.toString())
                        current.clear()
                    }
                    // Skip multiple spaces
                    while (i + 1 < cleanCommand.length && cleanCommand[i + 1] == ' ') {
                        i++
                    }
                }
                else -> {
                    current.append(char)
                }
            }
            i++
        }

        if (current.isNotEmpty()) {
            parts.add(current.toString())
        }

        return parts
    }
    
    private fun parseHttpMethod(method: String): HttpMethod {
        return when (method.uppercase()) {
            "GET" -> HttpMethod.GET
            "POST" -> HttpMethod.POST
            "PUT" -> HttpMethod.PUT
            "DELETE" -> HttpMethod.DELETE
            "PATCH" -> HttpMethod.PATCH
            "HEAD" -> HttpMethod.HEAD
            "OPTIONS" -> HttpMethod.OPTIONS
            else -> HttpMethod.GET
        }
    }
    
    private fun parseHeader(header: String): Pair<String, String>? {
        val colonIndex = header.indexOf(':')
        if (colonIndex == -1) return null

        val key = header.substring(0, colonIndex).trim()
        val value = header.substring(colonIndex + 1).trim()
        return key to value
    }
    
    private fun parseQueryParams(queryString: String): Map<String, String> {
        val params = mutableMapOf<String, String>()
        queryString.split("&").forEach { param ->
            val parts = param.split("=", limit = 2)
            if (parts.size == 2) {
                params[parts[0]] = parts[1]
            }
        }
        return params
    }
}

object CurlGenerator {
    
    fun generateCurl(
        url: String,
        method: HttpMethod,
        headers: Map<String, String>,
        queryParams: Map<String, String>,
        body: String?
    ): String {
        val curlBuilder = StringBuilder("curl")
        
        // Add method if not GET
        if (method != HttpMethod.GET) {
            curlBuilder.append(" -X ${method.name}")
        }
        
        // Add headers
        headers.forEach { (key, value) ->
            curlBuilder.append(" -H \"$key: $value\"")
        }
        
        // Add body if present
        if (!body.isNullOrBlank()) {
            curlBuilder.append(" -d '${body.replace("'", "\\'")}'")
        }
        
        // Build final URL with query parameters
        val finalUrl = if (queryParams.isNotEmpty()) {
            val queryString = queryParams.entries.joinToString("&") { "${it.key}=${it.value}" }
            "$url?$queryString"
        } else {
            url
        }
        
        curlBuilder.append(" \"$finalUrl\"")
        
        return curlBuilder.toString()
    }
}
