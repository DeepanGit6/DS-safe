package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.EmergencyContact
import com.example.data.model.SafePlace
import com.example.data.model.SafetySession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SafetySession::class,
        SafePlace::class,
        EmergencyContact::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SafeDSDatabase : RoomDatabase() {

    abstract fun safeDSDao(): SafeDSDao

    companion object {
        @Volatile
        private var INSTANCE: SafeDSDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SafeDSDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SafeDSDatabase::class.java,
                    "safe_ds_database"
                )
                    .addCallback(SafeDSDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class SafeDSDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.safeDSDao())
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        if (database.safeDSDao().getSessionCount() == 0) {
                            populateDatabase(database.safeDSDao())
                        }
                    }
                }
            }

            suspend fun populateDatabase(dao: SafeDSDao) {
                val now = System.currentTimeMillis()
                val oneDay = 86_400_000L

                // Default Safe Places matching the UI
                dao.insertSafePlaces(
                    listOf(
                        SafePlace(
                            name = "Home",
                            address = "42 Green Garden Avenue, Peelamedu",
                            latitude = 11.0168,
                            longitude = 76.9558,
                            isDefault = true,
                            isSafeZone = true,
                            autoCheckin = true,
                            iconType = "home"
                        ),
                        SafePlace(
                            name = "PSG Tech Campus",
                            address = "Avinashi Road, Peelamedu",
                            latitude = 11.0245,
                            longitude = 76.9680,
                            isDefault = false,
                            isSafeZone = true,
                            autoCheckin = true,
                            iconType = "school"
                        ),
                        SafePlace(
                            name = "City Central Library",
                            address = "D.B. Road, R.S. Puram",
                            latitude = 11.0089,
                            longitude = 76.9510,
                            isDefault = false,
                            isSafeZone = true,
                            autoCheckin = true,
                            iconType = "library"
                        ),
                        SafePlace(
                            name = "Hostel Block B",
                            address = "West Campus Gate 3",
                            latitude = 11.0260,
                            longitude = 76.9695,
                            isDefault = false,
                            isSafeZone = true,
                            autoCheckin = true,
                            iconType = "apartment"
                        )
                    )
                )

                // Default Emergency Contacts matching the UI
                dao.insertContacts(
                    listOf(
                        EmergencyContact(
                            name = "Mom (Kavitha)",
                            relation = "Mother",
                            phone = "+91 98765 43210",
                            isPrimary = true,
                            autoSmsEnabled = true,
                            avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCfjOxeaDtWU4_SFEcxRi8m-n1CXGuoiI14b0Ul3KA4TR-r4Wxeg08aD9fHtknKFpKOzzAvvySdF4N0ici-32-3wkeWnNHjtv1MDmSBLAavO9dfxw9MW5xIj8zpHuJ6U6LVu8-TDHMG-k49ffkbrCdLiUw3qHxc33T2CvoBP1JtE0KeABBDOG3Rn6sUEGjLe2lNp_8-weJYkStpAth_Ws7lnqKXiEuCOkmlG5Moz59YVJ6sToAYtJMp",
                            statusText = "Primary"
                        ),
                        EmergencyContact(
                            name = "Dad (Rajesh)",
                            relation = "Father",
                            phone = "+91 98765 43211",
                            isPrimary = false,
                            autoSmsEnabled = true,
                            avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAXaICBeZrmpOvQ6eJxZVQruE9nBVVGkZgQ0ao80YEZ3b0BZDpSDsOWvrUbR0udEqB6ka7id8gdRQCnkmzXuYIj2PSde_dEQkE4Fcwe97hYVzvSKdIwXsi5od7F9VbbUaEMaB8Hg3g2b-LaS9uEqrm5kyXxvWNgriA9944z5JDRIoJHWJbYipNQmOR-iJRozUEVbcOzO1hjON6yU1HFLmzxnL5qYySrgW5ZsVtRIvwMmw3j2BxuqnOs",
                            statusText = "Secondary"
                        ),
                        EmergencyContact(
                            name = "Pooja (Roommate)",
                            relation = "Friend",
                            phone = "+91 98765 43212",
                            isPrimary = false,
                            autoSmsEnabled = false,
                            avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDNb9MoOI3PPHvqs5ZOiTd7AHGF1K1vwQJlEvrliBjPrTMp8fvs4ci4E1j3bROJu7dsZfUCo6xgmWmu1Ql0v1oI213APem_PtVljOod5As2UY6wW0LjblVF3SYPoGq_ddvelDE06Aj1EqW5XKXNJb1FdI_pVitS4naRI1zf29DhTcWPg84mM-f-13uLohvVxqOIjdnVyy-MHYZDad4BgXyLSGtN4_WwF1gefr5WKAOc-W4jdySM4d2Y",
                            statusText = "Standby"
                        )
                    )
                )

                // Default Recent Safety Sessions matching the UI and supporting Weekly Trends
                dao.insertSessions(
                    listOf(
                        SafetySession(
                            title = "College → Home",
                            origin = "PSG Tech Campus",
                            destination = "Green Garden Home",
                            timeLabel = "Yesterday",
                            durationMinutes = 42,
                            distanceKm = 3.8,
                            status = "COMPLETED",
                            dayOfWeek = "Mon",
                            safetyScore = 100,
                            iconType = "school",
                            timestamp = now - oneDay
                        ),
                        SafetySession(
                            title = "Library → Hostel",
                            origin = "City Central Library",
                            destination = "Hostel Block B",
                            timeLabel = "2 days ago",
                            durationMinutes = 25,
                            distanceKm = 4.2,
                            status = "COMPLETED",
                            dayOfWeek = "Sun",
                            safetyScore = 100,
                            iconType = "library",
                            timestamp = now - (2 * oneDay)
                        ),
                        SafetySession(
                            title = "Station → Home",
                            origin = "Coimbatore Junction",
                            destination = "Green Garden Home",
                            timeLabel = "3 days ago",
                            durationMinutes = 55,
                            distanceKm = 8.5,
                            status = "COMPLETED",
                            dayOfWeek = "Sat",
                            safetyScore = 100,
                            iconType = "train",
                            timestamp = now - (3 * oneDay)
                        ),
                        SafetySession(
                            title = "Campus → Library",
                            origin = "PSG Tech Campus",
                            destination = "City Central Library",
                            timeLabel = "4 days ago",
                            durationMinutes = 30,
                            distanceKm = 3.1,
                            status = "COMPLETED",
                            dayOfWeek = "Fri",
                            safetyScore = 100,
                            iconType = "school",
                            timestamp = now - (4 * oneDay)
                        ),
                        SafetySession(
                            title = "Hostel → Campus",
                            origin = "Hostel Block B",
                            destination = "PSG Tech Campus",
                            timeLabel = "5 days ago",
                            durationMinutes = 18,
                            distanceKm = 1.2,
                            status = "COMPLETED",
                            dayOfWeek = "Thu",
                            safetyScore = 100,
                            iconType = "school",
                            timestamp = now - (5 * oneDay)
                        ),
                        SafetySession(
                            title = "Lab → Home",
                            origin = "Research Block",
                            destination = "Green Garden Home",
                            timeLabel = "6 days ago",
                            durationMinutes = 48,
                            distanceKm = 4.6,
                            status = "COMPLETED",
                            dayOfWeek = "Wed",
                            safetyScore = 98,
                            iconType = "school",
                            timestamp = now - (6 * oneDay)
                        ),
                        SafetySession(
                            title = "Gym → Hostel",
                            origin = "Fitness Centre",
                            destination = "Hostel Block B",
                            timeLabel = "7 days ago",
                            durationMinutes = 22,
                            distanceKm = 2.0,
                            status = "COMPLETED",
                            dayOfWeek = "Tue",
                            safetyScore = 100,
                            iconType = "default",
                            timestamp = now - (7 * oneDay)
                        )
                    )
                )
            }
        }
    }
}
