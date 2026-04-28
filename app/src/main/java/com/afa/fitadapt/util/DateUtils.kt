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

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT)
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
    @Suppress("unused") // Usata per export JSON
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
    @Suppress("unused") // Usata nei trend grafici e label brevi
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
    @Suppress("unused") // Utility per raggruppamento settimanale
    fun currentWeekNumber(): Int {
        val cal = Calendar.getInstance()
        return cal.get(Calendar.WEEK_OF_YEAR)
    }

    /**
     * Ottieni l'anno corrente.
     */
    @Suppress("unused") // Utility per header annuali
    fun currentYear(): Int {
        val cal = Calendar.getInstance()
        return cal.get(Calendar.YEAR)
    }

    /**
     * Calcola la differenza in giorni tra due timestamp.
     */
    @Suppress("unused") // Utility per calcolo differenze di date
    fun daysBetween(from: Long, to: Long): Int {
        val diffMs = to - from
        return (diffMs / (24 * 60 * 60 * 1000)).toInt()
    }

    fun isToday(timestamp: Long): Boolean {
        val today = toDayTimestamp(System.currentTimeMillis())
        val target = toDayTimestamp(timestamp)
        return today == target
    }

    fun isSameDay(t1: Long, t2: Long): Boolean {
        return toDayTimestamp(t1) == toDayTimestamp(t2)
    }

    /**
     * Ottiene l'inizio della settimana corrente (Lunedì alle 00:00:00).
     */
    fun getStartOfWeek(timestamp: Long = System.currentTimeMillis()): Long {
        val cal = Calendar.getInstance(Locale.ITALY)
        cal.timeInMillis = timestamp
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        
        // Imposta al lunedì della settimana corrente
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        
        // Se cal è dopo il timestamp (perché era già domenica/sabato in una locale diversa),
        // sottrai una settimana. Ma con Locale.ITALY e MONDAY dovrebbe essere corretto.
        if (cal.timeInMillis > timestamp) {
            cal.add(Calendar.WEEK_OF_YEAR, -1)
        }
        
        return cal.timeInMillis
    }

    /**
     * Ottiene l'inizio del mese corrente.
     */
    fun getStartOfMonth(timestamp: Long = System.currentTimeMillis()): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    /**
     * Ottiene l'inizio dell'anno corrente.
     */
    fun getStartOfYear(timestamp: Long = System.currentTimeMillis()): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        cal.set(Calendar.DAY_OF_YEAR, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    /**
     * Verifica se un obiettivo deve essere resettato in base alla sua periodicità.
     */
    fun isPeriodExpired(lastUpdate: Long, periodType: String, customValue: Int?, customUnit: String?): Boolean {
        if (periodType == "none") return false
        
        val now = System.currentTimeMillis()
        val calLast = Calendar.getInstance()
        calLast.timeInMillis = lastUpdate
        
        val calNow = Calendar.getInstance()
        calNow.timeInMillis = now

        return when (periodType) {
            "weekly" -> {
                calLast.get(Calendar.WEEK_OF_YEAR) != calNow.get(Calendar.WEEK_OF_YEAR) ||
                calLast.get(Calendar.YEAR) != calNow.get(Calendar.YEAR)
            }
            "monthly" -> {
                calLast.get(Calendar.MONTH) != calNow.get(Calendar.MONTH) ||
                calLast.get(Calendar.YEAR) != calNow.get(Calendar.YEAR)
            }
            "bimonthly" -> {
                val monthsDiff = (calNow.get(Calendar.YEAR) - calLast.get(Calendar.YEAR)) * 12 + 
                                 (calNow.get(Calendar.MONTH) - calLast.get(Calendar.MONTH))
                monthsDiff >= 2
            }
            "yearly" -> {
                calLast.get(Calendar.YEAR) != calNow.get(Calendar.YEAR)
            }
            "custom" -> {
                if (customValue == null || customUnit == null) return false
                val diffMs = now - lastUpdate
                val periodMs = when (customUnit) {
                    "days" -> customValue * 24 * 60 * 60 * 1000L
                    "weeks" -> customValue * 7 * 24 * 60 * 60 * 1000L
                    "months" -> customValue * 30 * 24 * 60 * 60 * 1000L // Approssimato
                    else -> 0L
                }
                diffMs >= periodMs
            }
            else -> false
        }
    }
}
