// =============================================================
// KinApto - Attività Fisica Adattata
// Helper per le notifiche locali
// =============================================================
package com.kinapto.fitadapt.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.kinapto.fitadapt.MainActivity
import com.kinapto.fitadapt.R
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
        const val CHANNEL_REMINDER = "kinapto_reminder"
        const val CHANNEL_MISSED = "kinapto_missed_session"
        const val CHANNEL_MOTIVATIONAL = "kinapto_motivational"
        const val CHANNEL_SCHEDULED = "kinapto_scheduled"
        const val CHANNEL_ADAPTATION = "kinapto_adaptation"

        // ID notifiche
        const val NOTIFICATION_REMINDER = 1001
        const val NOTIFICATION_MISSED = 1002
        const val NOTIFICATION_MOTIVATIONAL = 1003
        const val NOTIFICATION_SCHEDULED = 1004
        const val NOTIFICATION_ADAPTATION = 1005
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
            context.getString(R.string.notif_channel_reminder),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.notif_channel_reminder_desc)
        }

        val missedChannel = NotificationChannel(
            CHANNEL_MISSED,
            context.getString(R.string.notif_channel_missed),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.notif_channel_missed_desc)
        }

        val motivationalChannel = NotificationChannel(
            CHANNEL_MOTIVATIONAL,
            context.getString(R.string.notif_channel_motivational),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.notif_channel_motivational_desc)
        }

        val scheduledChannel = NotificationChannel(
            CHANNEL_SCHEDULED,
            context.getString(R.string.notif_channel_scheduled),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.notif_channel_scheduled_desc)
        }

        val adaptationChannel = NotificationChannel(
            CHANNEL_ADAPTATION,
            context.getString(R.string.notif_channel_adaptation),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.notif_channel_adaptation_desc)
        }

        manager.createNotificationChannels(
            listOf(reminderChannel, missedChannel, motivationalChannel, scheduledChannel, adaptationChannel)
        )
    }

    /**
     * Mostra un promemoria allenamento.
     */
    fun showReminderNotification() {
        showNotification(
            channelId = CHANNEL_REMINDER,
            notificationId = NOTIFICATION_REMINDER,
            title = context.getString(R.string.notif_reminder_title),
            body = context.getString(R.string.notif_reminder_body)
        )
    }

    /**
     * Mostra un avviso di sessione saltata.
     */
    fun showMissedSessionNotification() {
        showNotification(
            channelId = CHANNEL_MISSED,
            notificationId = NOTIFICATION_MISSED,
            title = context.getString(R.string.notif_missed_title),
            body = context.getString(R.string.notif_missed_body)
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
            title = context.getString(R.string.notif_motivational_title),
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
            title = context.getString(R.string.notif_scheduled_title, sessionTitle),
            body = context.getString(R.string.notif_scheduled_body)
        )
    }

    /**
     * Mostra una notifica quando la scheda viene adattata.
     */
    fun showAdaptationNotification(title: String, message: String) {
        showNotification(
            channelId = CHANNEL_ADAPTATION,
            notificationId = NOTIFICATION_ADAPTATION,
            title = title,
            body = message
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
