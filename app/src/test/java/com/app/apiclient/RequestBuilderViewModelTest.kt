package com.app.apiclient

import com.app.apiclient.data.model.*
import com.app.apiclient.data.repository.ApiRequestRepository
import com.app.apiclient.data.repository.EnvironmentRepository
import com.app.apiclient.ui.screens.request.RequestBuilderViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class RequestBuilderViewModelTest {

    private lateinit var viewModel: RequestBuilderViewModel
    private lateinit var apiRequestRepository: ApiRequestRepository
    private lateinit var environmentRepository: EnvironmentRepository
    
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        apiRequestRepository = mockk()
        environmentRepository = mockk()
        
        // Mock environment repository methods
        coEvery { environmentRepository.replaceVariables(any()) } returns "https://jsonplaceholder.typicode.com/posts/1"
        coEvery { environmentRepository.replaceVariablesInMap(any()) } returns mapOf("Accept" to "application/json")
        
        viewModel = RequestBuilderViewModel(apiRequestRepository, environmentRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have default values`() {
        val initialState = viewModel.uiState.value
        
        assertEquals("GET", initialState.selectedMethod)
        assertEquals("{{baseUrl}}/posts/{{userId}}", initialState.url)
        assertFalse(initialState.isLoading)
        assertNull(initialState.response)
        assertNull(initialState.error)
    }

    @Test
    fun `updateMethod should update selected method`() {
        viewModel.updateMethod("POST")
        
        assertEquals("POST", viewModel.uiState.value.selectedMethod)
    }

    @Test
    fun `updateUrl should update URL`() {
        val newUrl = "https://api.example.com/test"
        viewModel.updateUrl(newUrl)
        
        assertEquals(newUrl, viewModel.uiState.value.url)
    }

    @Test
    fun `addQueryParam should add new query parameter`() {
        viewModel.addQueryParam()
        
        val queryParams = viewModel.uiState.value.queryParams
        assertEquals(2, queryParams.size) // Initial has 1, we added 1
        assertEquals("" to "", queryParams.last())
    }

    @Test
    fun `loadSampleRequest should load GET_POSTS sample`() {
        viewModel.loadSampleRequest("GET_POSTS")
        
        val state = viewModel.uiState.value
        assertEquals("GET", state.selectedMethod)
        assertEquals("{{baseUrl}}/posts", state.url)
        assertEquals("None", state.bodyType)
        assertTrue(state.queryParams.any { it.first == "_limit" && it.second == "10" })
    }

    @Test
    fun `loadSampleRequest should load POST_USER sample`() {
        viewModel.loadSampleRequest("POST_USER")
        
        val state = viewModel.uiState.value
        assertEquals("POST", state.selectedMethod)
        assertEquals("{{baseUrl}}/users", state.url)
        assertEquals("Raw JSON", state.bodyType)
        assertTrue(state.body.contains("John Doe"))
        assertTrue(state.headers.any { it.first == "Content-Type" && it.second == "application/json" })
    }
}
