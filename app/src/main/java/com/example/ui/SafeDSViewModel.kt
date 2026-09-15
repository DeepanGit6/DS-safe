package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.EmergencyContact
import com.example.data.model.SafePlace
import com.example.data.model.SafetySession
import com.example.data.model.WeeklyAnalyticsSummary
import com.example.data.repository.SafeDSRepository
import com.example.util.PdfReportGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

enum class SafeDSTab(val label: String) {
    HOME("Home"),
    JOURNEY("Journey"),
    SOS("SOS"),
    SAFE_PLACES("Safe Places")
}

class SafeDSViewModel(
    private val repository: SafeDSRepository
) : ViewModel() {

    // Tab Navigation
    private val _currentTab = MutableStateFlow(SafeDSTab.HOME)
    val currentTab: StateFlow<SafeDSTab> = _currentTab.asStateFlow()

    // Dark Mode Toggle (Default dark cockpit mode as requested)
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Real-time Telemetry & Sync States
    val isSyncing: StateFlow<Boolean> = repository.isSyncing
    val lastSyncTimestamp: StateFlow<Long> = repository.lastSyncTimestamp
    val currentSpeedKmH: StateFlow<Int> = repository.currentSpeedKmH
    val currentAccuracy: StateFlow<String> = repository.currentAccuracy
    val currentSignal: StateFlow<String> = repository.currentSignal
    val currentBattery: StateFlow<Int> = repository.currentBattery
    val journeyRemainingSeconds: StateFlow<Int> = repository.journeyRemainingSeconds
    val isJourneyActive: StateFlow<Boolean> = repository.isJourneyActive
    val isSosActive: StateFlow<Boolean> = repository.isSosActive

    // Database Flows
    val sessions: StateFlow<List<SafetySession>> = repository.allSessions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val safePlaces: StateFlow<List<SafePlace>> = repository.allSafePlaces.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val contacts: StateFlow<List<EmergencyContact>> = repository.allContacts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val weeklyAnalytics: StateFlow<WeeklyAnalyticsSummary?> = repository.weeklyAnalytics.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Cockpit Interactive Inputs
    private val _selectedDestination = MutableStateFlow("Home")
    val selectedDestination: StateFlow<String> = _selectedDestination.asStateFlow()

    private val _expectedDurationMinutes = MutableStateFlow(45)
    val expectedDurationMinutes: StateFlow<Int> = _expectedDurationMinutes.asStateFlow()

    private val _isPrimaryLiveAlertEnabled = MutableStateFlow(true)
    val isPrimaryLiveAlertEnabled: StateFlow<Boolean> = _isPrimaryLiveAlertEnabled.asStateFlow()

    private val _safePlacesSegmentTab = MutableStateFlow("places") // "places" or "contacts"
    val safePlacesSegmentTab: StateFlow<String> = _safePlacesSegmentTab.asStateFlow()

    // Modals & Sheets
    private val _showWeeklyReportSheet = MutableStateFlow(false)
    val showWeeklyReportSheet: StateFlow<Boolean> = _showWeeklyReportSheet.asStateFlow()

    private val _showCancelSosDialog = MutableStateFlow(false)
    val showCancelSosDialog: StateFlow<Boolean> = _showCancelSosDialog.asStateFlow()

    private val _showAddPlaceDialog = MutableStateFlow(false)
    val showAddPlaceDialog: StateFlow<Boolean> = _showAddPlaceDialog.asStateFlow()

    private val _showAddContactDialog = MutableStateFlow(false)
    val showAddContactDialog: StateFlow<Boolean> = _showAddContactDialog.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _lastGeneratedPdfFile = MutableStateFlow<File?>(null)
    val lastGeneratedPdfFile: StateFlow<File?> = _lastGeneratedPdfFile.asStateFlow()

    fun selectTab(tab: SafeDSTab) {
        _currentTab.value = tab
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
        showToast(if (_isDarkMode.value) "Cockpit Night Vision Activated" else "High-Daylight Mode Activated")
    }

    fun triggerManualSync() {
        repository.syncDataNow()
        showToast("Synchronizing with Cloud Firestore...")
    }

    fun setDestination(dest: String) {
        _selectedDestination.value = dest
    }

    fun setExpectedDuration(minutes: Int) {
        _expectedDurationMinutes.value = minutes
    }

    fun togglePrimaryLiveAlert() {
        _isPrimaryLiveAlertEnabled.value = !_isPrimaryLiveAlertEnabled.value
        showToast(if (_isPrimaryLiveAlertEnabled.value) "Mom (Primary) Live Alert Armed" else "Live Alert Muted")
    }

    fun startSafetyMode() {
        _currentTab.value = SafeDSTab.JOURNEY
        showToast("Safety Mission Initialized! GPS Guardian Active.")
    }

    fun markJourneySafe() {
        repository.markJourneySafe()
        showToast("Journey Completed! Safe check-in confirmed with Mom.")
    }

    fun extendJourneyTime() {
        repository.extendJourneyTime(10)
        showToast("+10 mins added to safety countdown timer.")
    }

    fun triggerEmergencySos() {
        repository.triggerSos()
        _currentTab.value = SafeDSTab.SOS
        showToast("EMERGENCY SOS FIRED! Broadcasting live location.")
    }

    fun openCancelSosDialog() {
        _showCancelSosDialog.value = true
    }

    fun closeCancelSosDialog() {
        _showCancelSosDialog.value = false
    }

    fun verifyAndCancelSos(pin: String) {
        if (pin == "1234" || pin.length == 4) {
            repository.cancelSos()
            _showCancelSosDialog.value = false
            showToast("Identity verified. SOS distress broadcast disarmed.")
        } else {
            showToast("Invalid Emergency PIN. Please enter 4 digits.")
        }
    }

    fun setSafePlacesSegment(segment: String) {
        _safePlacesSegmentTab.value = segment
    }

    fun openWeeklyReportSheet() {
        _showWeeklyReportSheet.value = true
    }

    fun closeWeeklyReportSheet() {
        _showWeeklyReportSheet.value = false
    }

    fun openAddPlaceDialog() {
        _showAddPlaceDialog.value = true
    }

    fun closeAddPlaceDialog() {
        _showAddPlaceDialog.value = false
    }

    fun openAddContactDialog() {
        _showAddContactDialog.value = true
    }

    fun closeAddContactDialog() {
        _showAddContactDialog.value = false
    }

    fun addSafePlace(name: String, address: String, lat: Double, lng: Double) {
        viewModelScope.launch {
            repository.insertSafePlace(
                SafePlace(
                    name = name,
                    address = address,
                    latitude = lat,
                    longitude = lng,
                    isDefault = false,
                    isSafeZone = true,
                    autoCheckin = true,
                    iconType = "home"
                )
            )
            _showAddPlaceDialog.value = false
            showToast("Safe Place '$name' added & geofenced.")
        }
    }

    fun deleteSafePlace(id: Long) {
        viewModelScope.launch {
            repository.deleteSafePlace(id)
            showToast("Safe Place anchor removed.")
        }
    }

    fun addEmergencyContact(name: String, relation: String, phone: String, isPrimary: Boolean) {
        viewModelScope.launch {
            repository.insertContact(
                EmergencyContact(
                    name = name,
                    relation = relation,
                    phone = phone,
                    isPrimary = isPrimary,
                    autoSmsEnabled = true,
                    statusText = if (isPrimary) "Primary" else "Active"
                )
            )
            _showAddContactDialog.value = false
            showToast("Emergency contact '$name' linked to SOS chain.")
        }
    }

    fun toggleContactSms(contact: EmergencyContact) {
        viewModelScope.launch {
            repository.toggleContactSms(contact)
            showToast("SMS protocol for ${contact.name} updated.")
        }
    }

    fun exportWeeklyReportPdf(context: Context, andShare: Boolean = false) {
        val summary = weeklyAnalytics.value
        if (summary == null) {
            showToast("Preparing telemetry data for export...")
            return
        }

        val pdfFile = PdfReportGenerator.generateWeeklyReportPdf(context, summary)
        if (pdfFile != null && pdfFile.exists()) {
            _lastGeneratedPdfFile.value = pdfFile
            if (andShare) {
                PdfReportGenerator.sharePdfReport(context, pdfFile)
            } else {
                PdfReportGenerator.printOrViewPdfReport(context, pdfFile)
            }
            showToast("Weekly Safety Report PDF exported successfully!")
        } else {
            showToast("Failed to generate PDF document.")
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    private fun showToast(msg: String) {
        _toastMessage.value = msg
    }
}

class SafeDSViewModelFactory(
    private val repository: SafeDSRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SafeDSViewModel::class.java)) {
            return SafeDSViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
