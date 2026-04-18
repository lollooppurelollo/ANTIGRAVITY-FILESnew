// =============================================================
// AFA - Attività Fisica Adattata
// Schedulatore dei Worker periodici
// =============================================================
package com.afa.fitadapt.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.afa.fitadapt.notification.workers.MissedSessionWorker
import com.afa.fitadapt.notification.workers.MotivationalWorker
import com.afa.fitadapt.notification.workers.ReminderWorker
import dagger.hilt.android.qualifiers.ApplicationContext

import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Schedulatore per tutti i Worker periodici dell'app.
 *
 * Worker schedulati:
 * 1. ReminderWorker — ogni giorno all'orario impostato
 * 2. MissedSessionWorker — ogni giorno al mattino (8:00)
 * 3. MotivationalWorker — ogni 2-3 giorni
 *
 * Chiamato all'avvio dell'app da AfaApplication.
 */
@Singleton
class WorkScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Schedula tutti i Worker periodici.
     * Usa KEEP per non creare duplicati se i worker sono già schedulati.
     */
    fun scheduleAll() {
        scheduleDailyReminder()
        scheduleMissedSessionCheck()
        scheduleMotivationalMessages()
    }

    /**
     * Promemoria allenamento — ogni 24 ore.
     * L'orario esatto dipende da quando viene schedulato + l'intervallo minimo.
     * WorkManager non garantisce esecuzione esatta, ma entro una finestra.
     */
    private fun scheduleDailyReminder() {
        val reminderWork = PeriodicWorkRequestBuilder<ReminderWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            flexTimeInterval = 2,
            flexTimeIntervalUnit = TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            ReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            reminderWork
        )
    }

    /**
     * Controllo sessione saltata — ogni 24 ore.
     */
    private fun scheduleMissedSessionCheck() {
        val missedWork = PeriodicWorkRequestBuilder<MissedSessionWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            flexTimeInterval = 2,
            flexTimeIntervalUnit = TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            MissedSessionWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            missedWork
        )
    }

    /**
     * Messaggi motivazionali — ogni 2 giorni.
     */
    fun scheduleMotivationalMessages() {
        val motivationalWork = PeriodicWorkRequestBuilder<MotivationalWorker>(
            repeatInterval = 48,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            flexTimeInterval = 6,
            flexTimeIntervalUnit = TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            MotivationalWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            motivationalWork
        )
    }

    /**
     * Cancella tutti i Worker schedulati (utile quando le notifiche vengono disattivate).
     */
    fun cancelAll() {
        val wm = WorkManager.getInstance(context)
        wm.cancelUniqueWork(ReminderWorker.WORK_NAME)
        wm.cancelUniqueWork(MissedSessionWorker.WORK_NAME)
        wm.cancelUniqueWork(MotivationalWorker.WORK_NAME)
    }

    /** Cancella solo il motivational worker */
    fun cancelMotivational() {
        WorkManager.getInstance(context).cancelUniqueWork(MotivationalWorker.WORK_NAME)
    }
}
