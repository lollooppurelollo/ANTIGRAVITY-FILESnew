// =============================================================
// AFA - Attività Fisica Adattata
// Utility: Funzioni per la gestione delle date
// =============================================================
package com.afa.fitadapt.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Funzioni di utilità per la gestione delle date.
 * Usate in tutta l'app per conversioni e calcoli temporali.
 */
object DateUtils {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    private val displayFormat = SimpleDateFormat("d MMMM yyyy", Locale.ITALIAN)
    private val shortFormat = SimpleDateFormat("d MMM", Locale.ITALIAN)

    /**
     * Converte un timestamp in stringa ISO (per export JSON).
     * Es: "2026-04-13T07:30:00Z"
     */
    fun toIsoString(timestamp: Long): String =
        dateTimeFormat.format(timestamp)

    /**
     * Converte un timestamp in stringa data.
     * Es: "2026-04-13"
     */
    fun toDateString(timestamp: Long): String =
        dateFormat.format(timestamp)

    /**
     * Converte un timestamp in formato leggibile italiano.
     * Es: "13 aprile 2026"
     */
    fun toDisplayString(timestamp: Long): String =
        displayFormat.format(timestamp)

    /**
     * Converte un timestamp in formato corto.
     * Es: "13 apr"
     */
    fun toShortString(timestamp: Long): String =
        shortFormat.format(timestamp)

    /**
     * Ottieni l'intervallo del giorno corrente (inizio e fine).
     * Utile per query "oggi".
     *
     * @return Pair(inizioGiorno, fineGiorno) in millisecondi
     */
    fun todayRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfDay = cal.timeInMillis

        cal.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = cal.timeInMillis

        return Pair(startOfDay, endOfDay)
    }

    /**
     * Normalizza un timestamp al giorno (mezzanotte).
     * Usato per confrontare date senza considerare l'ora.
     */
    fun toDayTimestamp(timestamp: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    /**
     * Ottieni il timestamp di N giorni fa.
     */
    fun daysAgo(days: Int): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, -days)
        return cal.timeInMillis
    }

    /**
     * Ottieni il numero della settimana corrente nell'anno.
     */
    fun currentWeekNumber(): Int {
        val cal = Calendar.getInstance()
        return cal.get(Calendar.WEEK_OF_YEAR)
    }

    /**
     * Ottieni l'anno corrente.
     */
    fun currentYear(): Int {
        val cal = Calendar.getInstance()
        return cal.get(Calendar.YEAR)
    }

    /**
     * Calcola la differenza in giorni tra due timestamp.
     */
    fun daysBetween(from: Long, to: Long): Int {
        val diffMs = to - from
        return (diffMs / (24 * 60 * 60 * 1000)).toInt()
    }
}
