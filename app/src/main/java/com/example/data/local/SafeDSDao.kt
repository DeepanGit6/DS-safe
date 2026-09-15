package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.EmergencyContact
import com.example.data.model.SafePlace
import com.example.data.model.SafetySession
import kotlinx.coroutines.flow.Flow

@Dao
interface SafeDSDao {

    // Safety Sessions
    @Query("SELECT * FROM safety_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<SafetySession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SafetySession): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<SafetySession>)

    @Query("SELECT COUNT(*) FROM safety_sessions")
    suspend fun getSessionCount(): Int

    // Safe Places
    @Query("SELECT * FROM safe_places ORDER BY isDefault DESC, id ASC")
    fun getAllSafePlaces(): Flow<List<SafePlace>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSafePlace(place: SafePlace): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSafePlaces(places: List<SafePlace>)

    @Update
    suspend fun updateSafePlace(place: SafePlace)

    @Query("DELETE FROM safe_places WHERE id = :id")
    suspend fun deleteSafePlace(id: Long)

    @Query("SELECT COUNT(*) FROM safe_places")
    suspend fun getSafePlaceCount(): Int

    // Emergency Contacts
    @Query("SELECT * FROM emergency_contacts ORDER BY isPrimary DESC, id ASC")
    fun getAllContacts(): Flow<List<EmergencyContact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: EmergencyContact): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<EmergencyContact>)

    @Update
    suspend fun updateContact(contact: EmergencyContact)

    @Query("DELETE FROM emergency_contacts WHERE id = :id")
    suspend fun deleteContact(id: Long)

    @Query("SELECT COUNT(*) FROM emergency_contacts")
    suspend fun getContactCount(): Int
}
