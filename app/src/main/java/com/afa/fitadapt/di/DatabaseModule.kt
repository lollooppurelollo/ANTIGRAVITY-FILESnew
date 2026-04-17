// =============================================================
// AFA - Attività Fisica Adattata
// Modulo Hilt: Database con SQLCipher
// =============================================================
package com.afa.fitadapt.di

import android.content.Context
import androidx.room.Room
import com.afa.fitadapt.data.local.dao.ArticleDao
import com.afa.fitadapt.data.local.dao.DiaryDao
import com.afa.fitadapt.data.local.dao.ExerciseDao
import com.afa.fitadapt.data.local.dao.ExportLogDao
import com.afa.fitadapt.data.local.dao.GoalDao
import com.afa.fitadapt.data.local.dao.PatientProfileDao
import com.afa.fitadapt.data.local.dao.ScaleEntryDao
import com.afa.fitadapt.data.local.dao.SessionDao
import com.afa.fitadapt.data.local.dao.TrainingCardDao
import com.afa.fitadapt.data.local.db.AfaDatabase
import com.afa.fitadapt.security.CryptoManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

/**
 * Modulo Hilt per la creazione del database Room con cifratura SQLCipher.
 *
 * COME FUNZIONA L'INTEGRAZIONE SQLCipher + Room:
 *
 * 1. CryptoManager genera o recupera la passphrase (protetta dal Keystore)
 * 2. La passphrase viene convertita in byte[] per SQLCipher
 * 3. Si crea un SupportFactory con la passphrase
 * 4. Il SupportFactory viene passato a Room come openHelperFactory
 * 5. Da questo momento in poi, tutti i dati scritti su disco sono cifrati
 * 6. Il file .db è illeggibile senza la passphrase corretta
 *
 * Il database è un Singleton: viene creato una volta e riusato per tutta
 * la vita dell'app. Ogni DAO è fornito dal database come Singleton.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Crea il database Room cifrato con SQLCipher.
     *
     * Questa è l'integrazione REALE e completa:
     * - La passphrase è generata casualmente (32 byte / 256 bit)
     * - La passphrase è cifrata con AES-256-GCM (chiave nel Keystore)
     * - La passphrase cifrata è salvata nel DataStore
     * - Ad ogni apertura: decifra passphrase → apri DB
     */
    @Provides
    @Singleton
    fun provideAfaDatabase(
        @ApplicationContext context: Context,
        cryptoManager: CryptoManager
    ): AfaDatabase {
        // Passo 1: Ottieni la passphrase (genera se primo avvio, altrimenti decifra)
        val passphrase = cryptoManager.getOrCreateDatabasePassphrase()

        // Passo 2: Converti la passphrase in byte[] come richiesto da SQLCipher
        // Usiamo String(passphrase) per ottenere il contenuto reale dell'array
        val passphraseBytes = String(passphrase).toByteArray(Charsets.UTF_8)

        // Passo 3: Crea il SupportFactory — questo è il ponte tra SQLCipher e Room
        val supportFactory = SupportFactory(passphraseBytes)

        // Passo 5: Costruisci il database Room con la factory SQLCipher
        return Room.databaseBuilder(
            context.applicationContext,
            AfaDatabase::class.java,
            "afa_v1.db"       // Nome del file database su disco
        )
            .openHelperFactory(supportFactory)   // ← QUESTA RIGA ABILITA LA CIFRATURA
            .fallbackToDestructiveMigration()     // Se lo schema cambia, ricrea il DB
            .build()
    }

    // ══════════════════════════════════════════════════════════
    // Provider per ogni singolo DAO
    // Hilt li inietta dove servono (nei Repository)
    // ══════════════════════════════════════════════════════════

    @Provides
    fun provideExerciseDao(db: AfaDatabase): ExerciseDao = db.exerciseDao()

    @Provides
    fun provideTrainingCardDao(db: AfaDatabase): TrainingCardDao = db.trainingCardDao()

    @Provides
    fun provideSessionDao(db: AfaDatabase): SessionDao = db.sessionDao()

    @Provides
    fun provideDiaryDao(db: AfaDatabase): DiaryDao = db.diaryDao()

    @Provides
    fun provideScaleEntryDao(db: AfaDatabase): ScaleEntryDao = db.scaleEntryDao()

    @Provides
    fun provideArticleDao(db: AfaDatabase): ArticleDao = db.articleDao()

    @Provides
    fun provideExportLogDao(db: AfaDatabase): ExportLogDao = db.exportLogDao()

    @Provides
    fun providePatientProfileDao(db: AfaDatabase): PatientProfileDao = db.patientProfileDao()

    @Provides
    fun provideGoalDao(db: AfaDatabase): GoalDao = db.goalDao()
}
