// =============================================================
// KinApto - Attività Fisica Adattata
// Classe Application con Hilt e WorkManager
// =============================================================
package com.kinapto.fitadapt

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.kinapto.fitadapt.notification.WorkScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Classe Application principale dell'app KinApto.
 *
 * Responsabilità:
 * 1. Inizializza Hilt for l'iniezione delle dipendenze (@HiltAndroidApp)
 * 2. Configura WorkManager con HiltWorkerFactory per supportare @HiltWorker
 * 3. Schedula i Worker periodici (promemoria, sessioni saltate, motivazionali)
 */
@HiltAndroidApp
class KinAptoApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var workScheduler: WorkScheduler

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        workScheduler.scheduleAll()
    }
}
