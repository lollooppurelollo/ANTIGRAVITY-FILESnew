package com.afa.fitadapt.notification.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.afa.fitadapt.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import androidx.hilt.work.HiltWorker

@HiltWorker
class ScheduledSessionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val notificationHelper: NotificationHelper,
    private val calendarRepository: com.afa.fitadapt.data.repository.CalendarRepository,
    private val workScheduler: com.afa.fitadapt.notification.WorkScheduler
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

        val nextDate = java.util.Calendar.getInstance()
        nextDate.timeInMillis = session.date

        when (session.recurrenceType) {
            "WEEKLY" -> {
                val recurrenceDays = session.recurrenceDays?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList()
                if (recurrenceDays.isEmpty()) {
                    nextDate.add(java.util.Calendar.DAY_OF_YEAR, 7)
                } else {
                    // Find the next day of week in the list
                    // Calendar.DAY_OF_WEEK: Sun=1, Mon=2, ..., Sat=7
                    // Our recurrenceDays: Mon=1, ..., Sun=7
                    val currentDow = nextDate.get(java.util.Calendar.DAY_OF_WEEK)
                    val mappedCurrentDow = if (currentDow == java.util.Calendar.SUNDAY) 7 else currentDow - 1
                    
                    val nextMappedDow = recurrenceDays.sorted().find { it > mappedCurrentDow } ?: recurrenceDays.minOrNull() ?: mappedCurrentDow
                    
                    var daysToAdd = nextMappedDow - mappedCurrentDow
                    if (daysToAdd <= 0) daysToAdd += 7
                    nextDate.add(java.util.Calendar.DAY_OF_YEAR, daysToAdd)
                }
            }
            "EVERY_X_DAYS" -> nextDate.add(java.util.Calendar.DAY_OF_YEAR, session.recurrenceValue)
        }

        // Aggiorna l'entità nel DB con la nuova data
        val updatedSession = session.copy(date = nextDate.timeInMillis)
        calendarRepository.updateScheduledSession(updatedSession)

        // Schedula la prossima notifica
        if (updatedSession.notificationEnabled) {
            val parts = updatedSession.startTime.split(":")
            val hour = parts.getOrNull(0)?.toIntOrNull() ?: 10
            val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
            
            nextDate.set(java.util.Calendar.HOUR_OF_DAY, hour)
            nextDate.set(java.util.Calendar.MINUTE, minute)
            nextDate.set(java.util.Calendar.SECOND, 0)
            nextDate.set(java.util.Calendar.MILLISECOND, 0)

            workScheduler.scheduleScheduledSession(sessionId, updatedSession.title, nextDate.timeInMillis)
        }
    }

    companion object {
        const val WORK_NAME_PREFIX = "scheduled_session_"
    }
}
