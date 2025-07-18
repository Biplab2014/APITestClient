package com.app.apiclient

import com.app.apiclient.data.model.HttpMethod
import com.app.apiclient.utils.CurlGenerator
import com.app.apiclient.utils.CurlParser
import org.junit.Test
import org.junit.Assert.*

class CurlParserTest {

    @Test
    fun `parseCurl should parse simple GET request`() {
        val curlCommand = "curl https://api.example.com/users"
        val result = CurlParser.parseCurl(curlCommand)
        
        assertNotNull(result)
        assertEquals("https://api.example.com/users", result!!.url)
        assertEquals(HttpMethod.GET, result.method)
        assertTrue(result.headers.isEmpty())
        assertNull(result.body)
        assertTrue(result.queryParams.isEmpty())
    }

    @Test
    fun `parseCurl should parse POST request with data`() {
        val curlCommand = "curl -X POST https://api.example.com/users -d '{\"name\":\"John\"}'"
        val result = CurlParser.parseCurl(curlCommand)
        
        assertNotNull(result)
        assertEquals("https://api.example.com/users", result!!.url)
        assertEquals(HttpMethod.POST, result.method)
        assertEquals("{\"name\":\"John\"}", result.body)
    }

    @Test
    fun `parseCurl should parse request with headers`() {
        val curlCommand = "curl -H \"Content-Type: application/json\" -H \"Authorization: Bearer token\" https://api.example.com/users"
        val result = CurlParser.parseCurl(curlCommand)
        
        assertNotNull(result)
        assertEquals("https://api.example.com/users", result!!.url)
        assertEquals(HttpMethod.GET, result.method)
        assertEquals("application/json", result.headers["Content-Type"])
        assertEquals("Bearer token", result.headers["Authorization"])
    }

    @Test
    fun `parseCurl should parse request with query parameters`() {
        val curlCommand = "curl https://api.example.com/users?page=1&limit=10"
        val result = CurlParser.parseCurl(curlCommand)
        
        assertNotNull(result)
        assertEquals("https://api.example.com/users", result!!.url)
        assertEquals("1", result.queryParams["page"])
        assertEquals("10", result.queryParams["limit"])
    }

    @Test
    fun `parseCurl should parse complex request`() {
        val curlCommand = """
            curl -X POST "https://api.example.com/users?source=app" \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer abc123" \
            -d '{"name":"John Doe","email":"john@example.com"}'
        """.trimIndent().replace("\n", " ").replace("\\", "")
        
        val result = CurlParser.parseCurl(curlCommand)
        
        assertNotNull(result)
        assertEquals("https://api.example.com/users", result!!.url)
        assertEquals(HttpMethod.POST, result.method)
        assertEquals("application/json", result.headers["Content-Type"])
        assertEquals("Bearer abc123", result.headers["Authorization"])
        assertEquals("app", result.queryParams["source"])
        assertEquals("{\"name\":\"John Doe\",\"email\":\"john@example.com\"}", result.body)
    }

    @Test
    fun `parseCurl should handle invalid curl command`() {
        val curlCommand = "invalid command"
        val result = CurlParser.parseCurl(curlCommand)
        
        assertNull(result)
    }

    @Test
    fun `parseCurl should handle curl command without URL`() {
        val curlCommand = "curl -X POST -H \"Content-Type: application/json\""
        val result = CurlParser.parseCurl(curlCommand)
        
        assertNull(result)
    }

    @Test
    fun `generateCurl should create simple GET request`() {
        val result = CurlGenerator.generateCurl(
            url = "https://api.example.com/users",
            method = HttpMethod.GET,
            headers = emptyMap(),
            queryParams = emptyMap(),
            body = null
        )
        
        assertEquals("curl \"https://api.example.com/users\"", result)
    }

    @Test
    fun `generateCurl should create POST request with data`() {
        val result = CurlGenerator.generateCurl(
            url = "https://api.example.com/users",
            method = HttpMethod.POST,
            headers = mapOf("Content-Type" to "application/json"),
            queryParams = emptyMap(),
            body = "{\"name\":\"John\"}"
        )
        
        val expected = "curl -X POST -H \"Content-Type: application/json\" -d '{\"name\":\"John\"}' \"https://api.example.com/users\""
        assertEquals(expected, result)
    }

    @Test
    fun `generateCurl should create request with query parameters`() {
        val result = CurlGenerator.generateCurl(
            url = "https://api.example.com/users",
            method = HttpMethod.GET,
            headers = emptyMap(),
            queryParams = mapOf("page" to "1", "limit" to "10"),
            body = null
        )
        
        val expected = "curl \"https://api.example.com/users?page=1&limit=10\""
        assertEquals(expected, result)
    }

    @Test
    fun `generateCurl should create complex request`() {
        val result = CurlGenerator.generateCurl(
            url = "https://api.example.com/users",
            method = HttpMethod.PUT,
            headers = mapOf(
                "Content-Type" to "application/json",
                "Authorization" to "Bearer token123"
            ),
            queryParams = mapOf("source" to "mobile"),
            body = "{\"name\":\"Jane Doe\"}"
        )
        
        val expected = "curl -X PUT -H \"Content-Type: application/json\" -H \"Authorization: Bearer token123\" -d '{\"name\":\"Jane Doe\"}' \"https://api.example.com/users?source=mobile\""
        assertEquals(expected, result)
    }

    @Test
    fun `roundtrip test - parse then generate should be consistent`() {
        val originalCurl = "curl -X POST -H \"Content-Type: application/json\" -H \"Authorization: Bearer token\" -d '{\"test\":\"data\"}' \"https://api.example.com/test?param=value\""
        
        val parsed = CurlParser.parseCurl(originalCurl)
        assertNotNull(parsed)
        
        val regenerated = CurlGenerator.generateCurl(
            url = parsed!!.url,
            method = parsed.method,
            headers = parsed.headers,
            queryParams = parsed.queryParams,
            body = parsed.body
        )
        
        // Parse the regenerated curl to verify it's equivalent
        val reparsed = CurlParser.parseCurl(regenerated)
        assertNotNull(reparsed)
        
        assertEquals(parsed.url, reparsed!!.url)
        assertEquals(parsed.method, reparsed.method)
        assertEquals(parsed.headers, reparsed.headers)
        assertEquals(parsed.queryParams, reparsed.queryParams)
        assertEquals(parsed.body, reparsed.body)
    }
}
