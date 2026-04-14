// =============================================================
// AFA - Attività Fisica Adattata
// Worker: Controllo sessione saltata
// =============================================================
package com.afa.fitadapt.notification.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.afa.fitadapt.data.local.datastore.UserPreferences
import com.afa.fitadapt.data.repository.SessionRepository
import com.afa.fitadapt.notification.NotificationHelper
import com.afa.fitadapt.util.DateUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Worker per avviso sessione saltata.
 *
 * Viene eseguito al mattino per controllare se ieri è stata registrata
 * una sessione. Se mancante, invia un avviso gentile.
 *
 * Schedulato giornalmente (mattino presto) tramite WorkScheduler.
 */
@HiltWorker
class MissedSessionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userPreferences: UserPreferences,
    private val sessionRepository: SessionRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME = "afa_missed_session_check"
    }

    override suspend fun doWork(): Result {
        return try {
            // Verifica se le notifiche sono attive
            if (!userPreferences.isNotificationEnabled()) {
                return Result.success()
            }

            // Controlla se ieri c'è stata una sessione
            val yesterdayStart = DateUtils.daysAgo(1)
            val yesterdayEnd = DateUtils.toDayTimestamp(System.currentTimeMillis())

            // Deleghiamo al DAO direttamente tramite il repository
            val yesterdaySessions = sessionRepository.hasSessionYesterday(yesterdayStart, yesterdayEnd)

            if (!yesterdaySessions) {
                notificationHelper.showMissedSessionNotification()
            }

            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}
