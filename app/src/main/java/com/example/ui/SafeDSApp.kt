package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.ui.components.AddContactDialog
import com.example.ui.components.AddSafePlaceDialog
import com.example.ui.components.CancelSosDialog
import com.example.ui.components.SafeDSBottomBar
import com.example.ui.components.SafeDSHeader
import com.example.ui.components.WeeklyAnalyticsBottomSheet
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.JourneyScreen
import com.example.ui.screens.SafePlacesScreen
import com.example.ui.screens.SosScreen

@Composable
fun SafeDSApp(
    viewModel: SafeDSViewModel
) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    val sessions by viewModel.sessions.collectAsState()
    val safePlaces by viewModel.safePlaces.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val weeklyAnalytics by viewModel.weeklyAnalytics.collectAsState()

    val speedKmH by viewModel.currentSpeedKmH.collectAsState()
    val accuracy by viewModel.currentAccuracy.collectAsState()
    val signal by viewModel.currentSignal.collectAsState()
    val batteryPercent by viewModel.currentBattery.collectAsState()
    val remainingSeconds by viewModel.journeyRemainingSeconds.collectAsState()
    val isJourneyActive by viewModel.isJourneyActive.collectAsState()
    val isSosActive by viewModel.isSosActive.collectAsState()

    val selectedDestination by viewModel.selectedDestination.collectAsState()
    val expectedDurationMinutes by viewModel.expectedDurationMinutes.collectAsState()
    val isPrimaryLiveAlertEnabled by viewModel.isPrimaryLiveAlertEnabled.collectAsState()
    val safePlacesSegment by viewModel.safePlacesSegmentTab.collectAsState()

    // Dialog & Sheet States
    val showWeeklyReportSheet by viewModel.showWeeklyReportSheet.collectAsState()
    val showCancelSosDialog by viewModel.showCancelSosDialog.collectAsState()
    val showAddPlaceDialog by viewModel.showAddPlaceDialog.collectAsState()
    val showAddContactDialog by viewModel.showAddContactDialog.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            SafeDSHeader(
                isDarkMode = isDarkMode,
                isSyncing = isSyncing,
                onToggleDarkMode = { viewModel.toggleDarkMode() },
                onTriggerSync = { viewModel.triggerManualSync() }
            )
        },
        bottomBar = {
            SafeDSBottomBar(
                currentTab = currentTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { targetScreen ->
                when (targetScreen) {
                    SafeDSTab.HOME -> HomeScreen(
                        selectedDestination = selectedDestination,
                        expectedDurationMinutes = expectedDurationMinutes,
                        isPrimaryLiveAlertEnabled = isPrimaryLiveAlertEnabled,
                        sessions = sessions,
                        weeklyAnalytics = weeklyAnalytics,
                        onDestinationChange = { viewModel.setDestination(it) },
                        onDurationChange = { viewModel.setExpectedDuration(it) },
                        onTogglePrimaryAlert = { viewModel.togglePrimaryLiveAlert() },
                        onStartSafetyMode = { viewModel.startSafetyMode() },
                        onSosClick = { viewModel.triggerEmergencySos() },
                        onContactsClick = {
                            viewModel.setSafePlacesSegment("contacts")
                            viewModel.selectTab(SafeDSTab.SAFE_PLACES)
                        },
                        onSafeZonesClick = {
                            viewModel.setSafePlacesSegment("places")
                            viewModel.selectTab(SafeDSTab.SAFE_PLACES)
                        },
                        onHistoryClick = { viewModel.openWeeklyReportSheet() },
                        onExportPdf = { ctx, andShare -> viewModel.exportWeeklyReportPdf(ctx, andShare) }
                    )

                    SafeDSTab.JOURNEY -> JourneyScreen(
                        remainingSeconds = remainingSeconds,
                        isJourneyActive = isJourneyActive,
                        speedKmH = speedKmH,
                        accuracy = accuracy,
                        signal = signal,
                        batteryPercent = batteryPercent,
                        onMarkSafe = { viewModel.markJourneySafe() },
                        onExtendTime = { viewModel.extendJourneyTime() },
                        onTriggerSos = { viewModel.triggerEmergencySos() }
                    )

                    SafeDSTab.SOS -> SosScreen(
                        isSosActive = isSosActive,
                        onTriggerSos = { viewModel.triggerEmergencySos() },
                        onOpenCancelDialog = { viewModel.openCancelSosDialog() }
                    )

                    SafeDSTab.SAFE_PLACES -> SafePlacesScreen(
                        currentSegment = safePlacesSegment,
                        safePlaces = safePlaces,
                        contacts = contacts,
                        onSegmentChange = { viewModel.setSafePlacesSegment(it) },
                        onAddPlaceClick = { viewModel.openAddPlaceDialog() },
                        onAddContactClick = { viewModel.openAddContactDialog() },
                        onDeletePlace = { viewModel.deleteSafePlace(it) },
                        onToggleContactSms = { viewModel.toggleContactSms(it) }
                    )
                }
            }
        }
    }

    // Modal Sheets and Dialogs
    if (showWeeklyReportSheet) {
        WeeklyAnalyticsBottomSheet(
            summary = weeklyAnalytics,
            onDismiss = { viewModel.closeWeeklyReportSheet() },
            onExportPdf = { ctx, andShare -> viewModel.exportWeeklyReportPdf(ctx, andShare) }
        )
    }

    if (showCancelSosDialog) {
        CancelSosDialog(
            onDismiss = { viewModel.closeCancelSosDialog() },
            onConfirmCancel = { pin -> viewModel.verifyAndCancelSos(pin) }
        )
    }

    if (showAddPlaceDialog) {
        AddSafePlaceDialog(
            onDismiss = { viewModel.closeAddPlaceDialog() },
            onAddPlace = { name, address, lat, lng ->
                viewModel.addSafePlace(name, address, lat, lng)
            }
        )
    }

    if (showAddContactDialog) {
        AddContactDialog(
            onDismiss = { viewModel.closeAddContactDialog() },
            onAddContact = { name, relation, phone, isPrimary ->
                viewModel.addEmergencyContact(name, relation, phone, isPrimary)
            }
        )
    }
}
