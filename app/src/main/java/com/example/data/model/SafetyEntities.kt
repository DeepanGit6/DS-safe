package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "safety_sessions")
data class SafetySession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val origin: String,
    val destination: String,
    val timeLabel: String,
    val durationMinutes: Int,
    val distanceKm: Double,
    val status: String = "COMPLETED",
    val dayOfWeek: String, // Mon, Tue, Wed, Thu, Fri, Sat, Sun
    val safetyScore: Int = 100,
    val iconType: String = "school", // school, library, train, work, default
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "safe_places")
data class SafePlace(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val isDefault: Boolean = false,
    val isSafeZone: Boolean = true,
    val autoCheckin: Boolean = true,
    val iconType: String = "home" // home, school, library, apartment
)

@Entity(tableName = "emergency_contacts")
data class EmergencyContact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val relation: String,
    val phone: String,
    val isPrimary: Boolean = false,
    val autoSmsEnabled: Boolean = true,
    val avatarUrl: String = "",
    val statusText: String = "Active"
)

data class WeeklyDayStat(
    val day: String,
    val fullDayName: String,
    val sessionCount: Int,
    val totalMinutes: Int,
    val distanceKm: Double,
    val safetyScore: Int
)

data class WeeklyAnalyticsSummary(
    val totalSessions: Int,
    val totalMinutes: Int,
    val totalDistanceKm: Double,
    val avgSafetyScore: Int,
    val safeStreakDays: Int,
    val geofenceAutoCheckins: Int,
    val zeroIncidentStreak: Boolean,
    val dailyStats: List<WeeklyDayStat>
)
