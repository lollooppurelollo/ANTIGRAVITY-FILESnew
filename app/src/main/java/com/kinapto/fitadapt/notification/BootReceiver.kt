package com.kinapto.fitadapt.notification

import android.content.Context
import android.content.Intent
import androidx.work.Constraints
import androidx.work.NetworkType
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.content.BroadcastReceiver

/**
 * Receiver che intercetta l'avvio del dispositivo (BOOT_COMPLETED).
 * Fondamentale per ri-schedulare i promemoria medici di WorkManager
 * dopo uno spegnimento/riavvio, garantendo la continuità del protocollo clinico.
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var workScheduler: WorkScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Re-schedula tutti i task periodici
            workScheduler.scheduleAll()
        }
    }
}
