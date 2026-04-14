// =============================================================
// AFA - Attività Fisica Adattata
// Type Converters per Room
// =============================================================
package com.afa.fitadapt.data.local.db

import androidx.room.TypeConverter
import java.util.Date

/**
 * Convertitori di tipo per Room.
 * Permettono a Room di salvare e leggere tipi complessi nel database.
 *
 * Per ora gestiamo solo la conversione Date <-> Long (timestamp).
 * Se in futuro servissero altre conversioni (liste, mappe, ecc.),
 * si aggiungono qui.
 */
class Converters {

    // Converte un timestamp Long in un oggetto Date
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    // Converte un oggetto Date in un timestamp Long
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
