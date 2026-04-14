// =============================================================
// AFA - Attività Fisica Adattata
// Classe Application con Hilt e WorkManager
// =============================================================
package com.afa.fitadapt

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.afa.fitadapt.notification.WorkScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Classe Application principale dell'app AFA.
 *
 * Responsabilità:
 * 1. Inizializza Hilt per l'iniezione delle dipendenze (@HiltAndroidApp)
 * 2. Configura WorkManager con HiltWorkerFactory per supportare @HiltWorker
 * 3. Schedula i Worker periodici (promemoria, sessioni saltate, motivazionali)
 *
 * L'inizializzazione di WorkManager è manuale (non automatica) perché
 * usiamo Hilt per iniettare dipendenze nei Worker. Per questo motivo,
 * nell'AndroidManifest abbiamo disabilitato l'inizializzatore automatico.
 */
@HiltAndroidApp
class AfaApplication : Application(), Configuration.Provider {

    // HiltWorkerFactory viene iniettata automaticamente da Hilt.
    // Permette di usare @HiltWorker nei Worker per ricevere dipendenze.
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    // WorkScheduler gestisce la pianificazione dei Worker periodici.
    @Inject
    lateinit var workScheduler: WorkScheduler

    /**
     * Configurazione personalizzata di WorkManager.
     * Usa la HiltWorkerFactory per creare Worker con dipendenze iniettate.
     */
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        // Schedula tutti i Worker periodici (promemoria, sessione saltata, motivazionali)
        // WorkManager gestisce la deduplicazione automaticamente (KEEP policy)
        workScheduler.scheduleAll()
    }
}
