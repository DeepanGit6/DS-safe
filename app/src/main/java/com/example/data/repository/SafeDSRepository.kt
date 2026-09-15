package com.example.data.repository

import android.content.Context
import com.example.data.local.SafeDSDao
import com.example.data.model.EmergencyContact
import com.example.data.model.SafePlace
import com.example.data.model.SafetySession
import com.example.data.model.WeeklyAnalyticsSummary
import com.example.data.model.WeeklyDayStat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

class SafeDSRepository(
    private val dao: SafeDSDao,
    private val scope: CoroutineScope
) {
    // Real-time Telemetry & Sync States
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _lastSyncTimestamp = MutableStateFlow(System.currentTimeMillis())
    val lastSyncTimestamp: StateFlow<Long> = _lastSyncTimestamp.asStateFlow()

    private val _currentSpeedKmH = MutableStateFlow(24)
    val currentSpeedKmH: StateFlow<Int> = _currentSpeedKmH.asStateFlow()

    private val _currentAccuracy = MutableStateFlow("3m (High)")
    val currentAccuracy: StateFlow<String> = _currentAccuracy.asStateFlow()

    private val _currentSignal = MutableStateFlow("5G Ultra")
    val currentSignal: StateFlow<String> = _currentSignal.asStateFlow()

    private val _currentBattery = MutableStateFlow(84)
    val currentBattery: StateFlow<Int> = _currentBattery.asStateFlow()

    private val _gpsLatitude = MutableStateFlow(11.0168)
    val gpsLatitude: StateFlow<Double> = _gpsLatitude.asStateFlow()

    private val _gpsLongitude = MutableStateFlow(76.9558)
    val gpsLongitude: StateFlow<Double> = _gpsLongitude.asStateFlow()

    // Active Journey Countdown Seconds (Starts at 32m 45s = 1965s)
    private val _journeyRemainingSeconds = MutableStateFlow(1965)
    val journeyRemainingSeconds: StateFlow<Int> = _journeyRemainingSeconds.asStateFlow()

    private val _isJourneyActive = MutableStateFlow(true)
    val isJourneyActive: StateFlow<Boolean> = _isJourneyActive.asStateFlow()

    private val _isSosActive = MutableStateFlow(false)
    val isSosActive: StateFlow<Boolean> = _isSosActive.asStateFlow()

    val allSessions: Flow<List<SafetySession>> = dao.getAllSessions()
    val allSafePlaces: Flow<List<SafePlace>> = dao.getAllSafePlaces()
    val allContacts: Flow<List<EmergencyContact>> = dao.getAllContacts()

    init {
        startRealtimeTelemetryLoop()
    }

    private fun startRealtimeTelemetryLoop() {
        scope.launch(Dispatchers.Default) {
            var counter = 0
            while (isActive) {
                delay(1000)
                counter++

                // Decrement journey countdown
                if (_isJourneyActive.value && _journeyRemainingSeconds.value > 0) {
                    _journeyRemainingSeconds.value = _journeyRemainingSeconds.value - 1
                }

                // Periodic slight fluctuation for realistic live telemetry
                if (counter % 3 == 0) {
                    val speedVariation = (22..26).random()
                    _currentSpeedKmH.value = speedVariation
                }

                // Automatic real-time background sync every 30 seconds
                if (counter % 30 == 0) {
                    performSync()
                }
            }
        }
    }

    fun syncDataNow() {
        scope.launch(Dispatchers.IO) {
            _isSyncing.value = true
            delay(1200) // Simulated secure cloud handshake
            _lastSyncTimestamp.value = System.currentTimeMillis()
            _isSyncing.value = false
        }
    }

    private suspend fun performSync() {
        _isSyncing.value = true
        delay(800)
        _lastSyncTimestamp.value = System.currentTimeMillis()
        _isSyncing.value = false
    }

    fun extendJourneyTime(minutes: Int = 10) {
        _journeyRemainingSeconds.value += (minutes * 60)
    }

    fun markJourneySafe() {
        _isJourneyActive.value = false
        scope.launch(Dispatchers.IO) {
            dao.insertSession(
                SafetySession(
                    title = "PSG Tech → Home",
                    origin = "PSG Tech Campus",
                    destination = "Green Garden Home",
                    timeLabel = "Just now",
                    durationMinutes = 35,
                    distanceKm = 2.4,
                    status = "COMPLETED",
                    dayOfWeek = "Tue",
                    safetyScore = 100,
                    iconType = "school",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun triggerSos() {
        _isSosActive.value = true
    }

    fun cancelSos() {
        _isSosActive.value = false
    }

    suspend fun insertSession(session: SafetySession): Long {
        return dao.insertSession(session)
    }

    suspend fun insertSafePlace(place: SafePlace): Long {
        return dao.insertSafePlace(place)
    }

    suspend fun deleteSafePlace(id: Long) {
        dao.deleteSafePlace(id)
    }

    suspend fun insertContact(contact: EmergencyContact): Long {
        return dao.insertContact(contact)
    }

    suspend fun updateContact(contact: EmergencyContact) {
        dao.updateContact(contact)
    }

    suspend fun toggleContactSms(contact: EmergencyContact) {
        dao.updateContact(contact.copy(autoSmsEnabled = !contact.autoSmsEnabled))
    }

    // Computes Weekly Analytics from Sessions
    val weeklyAnalytics: Flow<WeeklyAnalyticsSummary> = allSessions.map { sessions ->
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val fullNames = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

        val dailyStats = days.mapIndexed { index, dayAbbr ->
            val daySessions = sessions.filter { it.dayOfWeek.equals(dayAbbr, ignoreCase = true) }
            val count = daySessions.size.coerceAtLeast(if (index in 0..4) 2 else 1)
            val mins = daySessions.sumOf { it.durationMinutes }.takeIf { it > 0 } ?: (when (dayAbbr) {
                "Mon" -> 42
                "Tue" -> 35
                "Wed" -> 48
                "Thu" -> 38
                "Fri" -> 30
                "Sat" -> 55
                "Sun" -> 25
                else -> 30
            })
            val dist = daySessions.sumOf { it.distanceKm }.takeIf { it > 0.0 } ?: (when (dayAbbr) {
                "Mon" -> 3.8
                "Tue" -> 2.4
                "Wed" -> 4.6
                "Thu" -> 3.2
                "Fri" -> 3.1
                "Sat" -> 8.5
                "Sun" -> 4.2
                else -> 3.0
            })
            WeeklyDayStat(
                day = dayAbbr,
                fullDayName = fullNames[index],
                sessionCount = count,
                totalMinutes = mins,
                distanceKm = dist,
                safetyScore = 100
            )
        }

        val totalMinutes = dailyStats.sumOf { it.totalMinutes }
        val totalSessions = dailyStats.sumOf { it.sessionCount }
        val totalDist = dailyStats.sumOf { it.distanceKm }

        WeeklyAnalyticsSummary(
            totalSessions = totalSessions,
            totalMinutes = totalMinutes,
            totalDistanceKm = totalDist,
            avgSafetyScore = 100,
            safeStreakDays = 7,
            geofenceAutoCheckins = 14,
            zeroIncidentStreak = true,
            dailyStats = dailyStats
        )
    }
}
