// =============================================================
// AFA - Attività Fisica Adattata
// Worker: Promemoria allenamento quotidiano
// =============================================================
package com.afa.fitadapt.notification.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.afa.fitadapt.data.local.datastore.UserPreferences
import com.afa.fitadapt.data.repository.SessionRepository
import com.afa.fitadapt.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Worker per il promemoria allenamento quotidiano.
 *
 * Logica:
 * 1. Controlla se le notifiche sono attive
 * 2. Controlla se oggi è già stata registrata una sessione
 * 3. Se non è stata registrata, mostra il promemoria
 *
 * Schedulato giornalmente tramite WorkScheduler.
 */
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userPreferences: UserPreferences,
    private val sessionRepository: SessionRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME = "afa_daily_reminder"
    }

    override suspend fun doWork(): Result {
        return try {
            // Verifica se le notifiche sono attive
            if (!userPreferences.isNotificationEnabled()) {
                return Result.success()
            }

            // Verifica se oggi è stata già registrata una sessione
            val hasTodaySession = sessionRepository.hasSessionToday()

            if (!hasTodaySession) {
                // Mostra il promemoria
                notificationHelper.showReminderNotification()
            }

            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}
