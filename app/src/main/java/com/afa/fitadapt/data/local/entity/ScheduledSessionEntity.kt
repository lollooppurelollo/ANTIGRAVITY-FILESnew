package com.afa.fitadapt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scheduled_sessions")
data class ScheduledSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long, // Timestamp del giorno specifico
    val startTime: String, // HH:mm
    val title: String,
    val notificationEnabled: Boolean = false,
    val recurrenceType: String = "NONE", // NONE, WEEKLY, EVERY_X_DAYS
    val recurrenceValue: Int = 0, // Inutilizzato per WEEKLY (usiamo recurrenceDays)
    val recurrenceDays: String? = null, // Giorni della settimana per WEEKLY (es. "1,3,5")
    val cardId: Long? = null
)
