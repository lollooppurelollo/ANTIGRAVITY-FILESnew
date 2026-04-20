// =============================================================
// AFA - Attività Fisica Adattata
// Helper per le notifiche locali
// =============================================================
package com.afa.fitadapt.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.afa.fitadapt.MainActivity
import com.afa.fitadapt.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper per la gestione delle notifiche locali dell'app.
 *
 * Canali:
 * 1. REMINDER — Promemoria allenamento quotidiano
 * 2. MISSED — Avviso sessione saltata
 * 3. MOTIVATIONAL — Messaggi motivazionali
 *
 * Nessuna notifica viene mai inviata a server esterni.
 */
@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        // Canali
        const val CHANNEL_REMINDER = "afa_reminder"
        const val CHANNEL_MISSED = "afa_missed_session"
        const val CHANNEL_MOTIVATIONAL = "afa_motivational"
        const val CHANNEL_SCHEDULED = "afa_scheduled"

        // ID notifiche
        const val NOTIFICATION_REMINDER = 1001
        const val NOTIFICATION_MISSED = 1002
        const val NOTIFICATION_MOTIVATIONAL = 1003
        const val NOTIFICATION_SCHEDULED = 1004
    }

    init {
        createNotificationChannels()
    }

    /**
     * Crea i canali di notifica (richiesto da Android 8.0+).
     * Operazione idempotente — può essere chiamata più volte.
     */
    private fun createNotificationChannels() {
        val manager = context.getSystemService(NotificationManager::class.java)

        val reminderChannel = NotificationChannel(
            CHANNEL_REMINDER,
            "Promemoria allenamento",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Ricorda di fare l'allenamento quotidiano"
        }

        val missedChannel = NotificationChannel(
            CHANNEL_MISSED,
            "Sessione saltata",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Avviso quando una sessione non viene registrata"
        }

        val motivationalChannel = NotificationChannel(
            CHANNEL_MOTIVATIONAL,
            "Messaggi motivazionali",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Messaggi di incoraggiamento periodici"
        }

        val scheduledChannel = NotificationChannel(
            CHANNEL_SCHEDULED,
            "Allenamenti programmati",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifiche per gli allenamenti che hai programmato"
        }

        manager.createNotificationChannels(
            listOf(reminderChannel, missedChannel, motivationalChannel, scheduledChannel)
        )
    }

    /**
     * Mostra un promemoria allenamento.
     */
    fun showReminderNotification() {
        showNotification(
            channelId = CHANNEL_REMINDER,
            notificationId = NOTIFICATION_REMINDER,
            title = "È ora di muoversi! 🏃‍♀️",
            body = "Hai un allenamento in programma oggi. Anche pochi minuti fanno la differenza!"
        )
    }

    /**
     * Mostra un avviso di sessione saltata.
     */
    fun showMissedSessionNotification() {
        showNotification(
            channelId = CHANNEL_MISSED,
            notificationId = NOTIFICATION_MISSED,
            title = "Sessione non registrata",
            body = "Ieri non hai registrato l'allenamento. Ricorda di segnare le tue attività!"
        )
    }

    /**
     * Mostra un messaggio motivazionale.
     *
     * @param message il messaggio da mostrare
     */
    fun showMotivationalNotification(message: String) {
        showNotification(
            channelId = CHANNEL_MOTIVATIONAL,
            notificationId = NOTIFICATION_MOTIVATIONAL,
            title = "💪 Il tuo coach AFA",
            body = message
        )
    }

    /**
     * Mostra una notifica per una sessione programmata.
     */
    fun showScheduledSessionNotification(sessionTitle: String) {
        showNotification(
            channelId = CHANNEL_SCHEDULED,
            notificationId = (NOTIFICATION_SCHEDULED + System.currentTimeMillis() % 1000).toInt(),
            title = "È ora di: $sessionTitle 🏋️",
            body = "La tua sessione programmata inizia ora. Sei pronto?"
        )
    }

    /**
     * Metodo generico per mostrare una notifica.
     */
    private fun showNotification(
        channelId: String,
        notificationId: Int,
        title: String,
        body: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, notificationId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(notificationId, notification)
    }
}
