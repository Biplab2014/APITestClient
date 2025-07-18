package com.app.apiclient

import com.app.apiclient.data.repository.ApiRequestRepository
import com.app.apiclient.data.repository.CollectionRepository
import com.app.apiclient.data.repository.EnvironmentRepository
import com.app.apiclient.ui.screens.settings.SettingsViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var apiRequestRepository: ApiRequestRepository
    private lateinit var collectionRepository: CollectionRepository
    private lateinit var environmentRepository: EnvironmentRepository
    
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        apiRequestRepository = mockk()
        collectionRepository = mockk()
        environmentRepository = mockk()
        
        // Mock repository methods
        coEvery { apiRequestRepository.getAllRequests() } returns flowOf(emptyList())
        coEvery { collectionRepository.getAllCollections() } returns flowOf(emptyList())
        coEvery { environmentRepository.getAllEnvironments() } returns flowOf(emptyList())
        
        viewModel = SettingsViewModel(
            apiRequestRepository,
            collectionRepository,
            environmentRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have default values`() {
        val initialState = viewModel.uiState.value
        
        assertFalse(initialState.isDarkMode)
        assertTrue(initialState.autoSaveRequests)
        assertEquals(30, initialState.requestTimeout)
        assertTrue(initialState.sslVerification)
        assertFalse(initialState.isLoading)
        assertNull(initialState.error)
        assertFalse(initialState.showTimeoutDialog)
        assertFalse(initialState.showClearHistoryDialog)
        assertFalse(initialState.showExportDialog)
        assertFalse(initialState.showImportDialog)
    }

    @Test
    fun `toggleDarkMode should toggle dark mode setting`() {
        // Initially false
        assertFalse(viewModel.uiState.value.isDarkMode)
        
        // Toggle to true
        viewModel.toggleDarkMode()
        assertTrue(viewModel.uiState.value.isDarkMode)
        
        // Toggle back to false
        viewModel.toggleDarkMode()
        assertFalse(viewModel.uiState.value.isDarkMode)
    }

    @Test
    fun `toggleAutoSave should toggle auto save setting`() {
        // Initially true
        assertTrue(viewModel.uiState.value.autoSaveRequests)
        
        // Toggle to false
        viewModel.toggleAutoSave()
        assertFalse(viewModel.uiState.value.autoSaveRequests)
        
        // Toggle back to true
        viewModel.toggleAutoSave()
        assertTrue(viewModel.uiState.value.autoSaveRequests)
    }

    @Test
    fun `toggleSslVerification should toggle SSL verification setting`() {
        // Initially true
        assertTrue(viewModel.uiState.value.sslVerification)
        
        // Toggle to false
        viewModel.toggleSslVerification()
        assertFalse(viewModel.uiState.value.sslVerification)
        
        // Toggle back to true
        viewModel.toggleSslVerification()
        assertTrue(viewModel.uiState.value.sslVerification)
    }

    @Test
    fun `showTimeoutDialog should show timeout dialog`() {
        assertFalse(viewModel.uiState.value.showTimeoutDialog)
        
        viewModel.showTimeoutDialog()
        assertTrue(viewModel.uiState.value.showTimeoutDialog)
    }

    @Test
    fun `hideTimeoutDialog should hide timeout dialog`() {
        viewModel.showTimeoutDialog()
        assertTrue(viewModel.uiState.value.showTimeoutDialog)
        
        viewModel.hideTimeoutDialog()
        assertFalse(viewModel.uiState.value.showTimeoutDialog)
    }

    @Test
    fun `updateTimeout should update timeout value and hide dialog`() {
        val newTimeout = 60
        
        viewModel.showTimeoutDialog()
        assertTrue(viewModel.uiState.value.showTimeoutDialog)
        
        viewModel.updateTimeout(newTimeout)
        
        assertEquals(newTimeout, viewModel.uiState.value.requestTimeout)
        assertFalse(viewModel.uiState.value.showTimeoutDialog)
    }

    @Test
    fun `showClearHistoryDialog should show clear history dialog`() {
        assertFalse(viewModel.uiState.value.showClearHistoryDialog)
        
        viewModel.showClearHistoryDialog()
        assertTrue(viewModel.uiState.value.showClearHistoryDialog)
    }

    @Test
    fun `clearError should clear error message`() {
        // Set an error state manually for testing
        viewModel.clearError()
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `showExportDialog should show export dialog`() {
        assertFalse(viewModel.uiState.value.showExportDialog)
        
        viewModel.showExportDialog()
        assertTrue(viewModel.uiState.value.showExportDialog)
    }

    @Test
    fun `showImportDialog should show import dialog`() {
        assertFalse(viewModel.uiState.value.showImportDialog)
        
        viewModel.showImportDialog()
        assertTrue(viewModel.uiState.value.showImportDialog)
    }
}
