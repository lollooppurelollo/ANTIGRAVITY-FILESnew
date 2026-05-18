package com.kinapto.fitadapt.notification.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kinapto.fitadapt.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import androidx.hilt.work.HiltWorker

@HiltWorker
class ScheduledSessionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val notificationHelper: NotificationHelper,
    private val calendarRepository: com.kinapto.fitadapt.data.repository.CalendarRepository,
    private val workScheduler: com.kinapto.fitadapt.notification.WorkScheduler
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val sessionId = inputData.getLong("sessionId", -1)
        val title = inputData.getString("title") ?: "Allenamento"
        
        notificationHelper.showScheduledSessionNotification(title)

        if (sessionId != -1L) {
            handleRecurrence(sessionId)
        }

        return Result.success()
    }

    private suspend fun handleRecurrence(sessionId: Long) {
        val session = calendarRepository.getScheduledById(sessionId) ?: return
        if (session.recurrenceType == "NONE") return

        val now = java.util.Calendar.getInstance()
        val nextDate = java.util.Calendar.getInstance()
        nextDate.timeInMillis = session.date

        // Calcola la prossima data utile (deve essere nel futuro)
        do {
            when (session.recurrenceType) {
                "WEEKLY" -> {
                    val recurrenceDays = session.recurrenceDays?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList()
                    if (recurrenceDays.isEmpty()) {
                        nextDate.add(java.util.Calendar.DAY_OF_YEAR, 7)
                    } else {
                        val currentDow = nextDate.get(java.util.Calendar.DAY_OF_WEEK)
                        val mappedCurrentDow = if (currentDow == java.util.Calendar.SUNDAY) 7 else currentDow - 1
                        
                        val nextMappedDow = recurrenceDays.sorted().find { it > mappedCurrentDow } ?: recurrenceDays.minOrNull() ?: mappedCurrentDow
                        
                        var daysToAdd = nextMappedDow - mappedCurrentDow
                        if (daysToAdd <= 0) daysToAdd += 7
                        nextDate.add(java.util.Calendar.DAY_OF_YEAR, daysToAdd)
                    }
                }
                "EVERY_X_DAYS" -> nextDate.add(java.util.Calendar.DAY_OF_YEAR, session.recurrenceValue.coerceAtLeast(1))
                else -> break
            }
            
            // Imposta l'orario previsto per il controllo del "futuro"
            val parts = session.startTime.split(":")
            nextDate.set(java.util.Calendar.HOUR_OF_DAY, parts.getOrNull(0)?.toIntOrNull() ?: 10)
            nextDate.set(java.util.Calendar.MINUTE, parts.getOrNull(1)?.toIntOrNull() ?: 0)
            nextDate.set(java.util.Calendar.SECOND, 0)
            nextDate.set(java.util.Calendar.MILLISECOND, 0)

        } while (nextDate.before(now) && session.recurrenceType != "NONE")

        // 1. Crea la NUOVA sessione per la prossima occorrenza
        val newSession = session.copy(
            id = 0, 
            date = nextDate.timeInMillis
        )
        val newId = calendarRepository.addScheduledSession(newSession)

        // 2. Disattiva la ricorrenza sulla sessione attuale (diventa storica)
        val oldSessionProcessed = session.copy(recurrenceType = "NONE")
        calendarRepository.updateScheduledSession(oldSessionProcessed)

        // 3. Schedula la prossima notifica per il nuovo ID
        if (newSession.notificationEnabled) {
            workScheduler.scheduleScheduledSession(newId, newSession.title, nextDate.timeInMillis)
        }
    }

    companion object {
        const val WORK_NAME_PREFIX = "scheduled_session_"
    }
}
