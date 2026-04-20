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

        val nextDate = java.util.Calendar.getInstance().apply {
            timeInMillis = session.date
            when (session.recurrenceType) {
                "WEEKLY" -> add(java.util.Calendar.DAY_OF_YEAR, 7)
                "EVERY_X_DAYS" -> add(java.util.Calendar.DAY_OF_YEAR, session.recurrenceValue)
            }
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
