// =============================================================
// KinApto - Attività Fisica Adattata
// Modulo Hilt: Database con SQLCipher
// =============================================================
package com.kinapto.fitadapt.di

import android.content.Context
import androidx.room.Room
import com.kinapto.fitadapt.data.local.dao.ArticleDao
import com.kinapto.fitadapt.data.local.dao.AuditLogDao
import com.kinapto.fitadapt.data.local.dao.DiaryDao
import com.kinapto.fitadapt.data.local.dao.ExerciseDao
import com.kinapto.fitadapt.data.local.dao.ExportLogDao
import com.kinapto.fitadapt.data.local.dao.GoalDao
import com.kinapto.fitadapt.data.local.dao.PatientProfileDao
import com.kinapto.fitadapt.data.local.dao.ScaleEntryDao
import com.kinapto.fitadapt.data.local.dao.ScheduledSessionDao
import com.kinapto.fitadapt.data.local.dao.SessionDao
import com.kinapto.fitadapt.data.local.dao.TrainingCardDao
import com.kinapto.fitadapt.data.local.db.KinAptoDatabase
import com.kinapto.fitadapt.security.CryptoManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
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
    fun provideKinAptoDatabase(
        @ApplicationContext context: Context,
        cryptoManager: CryptoManager
    ): KinAptoDatabase {
        // Passo 1: Ottieni la passphrase (genera se primo avvio, altrimenti decifra)
        // Room richiede l'apertura sincrona dell'HelperFactory nel thread di apertura DB.
        // Poiché Hilt inietta il database come singleton, questa chiamata avviene
        // alla prima richiesta del database.
        val passphrase = runBlocking {
            cryptoManager.getOrCreateDatabasePassphrase()
        }

        // Passo 2: Converti la passphrase in byte[] come richiesto da SQLCipher
        val passphraseBytes = String(passphrase).toByteArray(Charsets.UTF_8)

        // Passo 3: Crea il SupportFactory — questo è il ponte tra SQLCipher e Room
        val supportFactory = SupportFactory(passphraseBytes)

        // Passo 5: Costruisci il database Room con la factory SQLCipher
        return Room.databaseBuilder(
            context.applicationContext,
            KinAptoDatabase::class.java,
            "kinapto_v1.db"       // Nome del file database su disco
        )
            .openHelperFactory(supportFactory)   // ← QUESTA RIGA ABILITA LA CIFRATURA
            .addMigrations(
                KinAptoDatabase.MIGRATION_4_5,
                KinAptoDatabase.MIGRATION_5_6,
                KinAptoDatabase.MIGRATION_6_7,
                KinAptoDatabase.MIGRATION_7_8,
                KinAptoDatabase.MIGRATION_8_9,
                KinAptoDatabase.MIGRATION_9_10
            )
            .fallbackToDestructiveMigration(dropAllTables = true)     // Se lo schema cambia e non c'è migrazione, ricrea il DB
            .build()
    }

    // ══════════════════════════════════════════════════════════
    // Provider per ogni singolo DAO
    // Hilt li inietta dove servono (nei Repository)
    // ══════════════════════════════════════════════════════════

    @Provides
    fun provideExerciseDao(db: KinAptoDatabase): ExerciseDao = db.exerciseDao()

    @Provides
    fun provideTrainingCardDao(db: KinAptoDatabase): TrainingCardDao = db.trainingCardDao()

    @Provides
    fun provideSessionDao(db: KinAptoDatabase): SessionDao = db.sessionDao()

    @Provides
    fun provideDiaryDao(db: KinAptoDatabase): DiaryDao = db.diaryDao()

    @Provides
    fun provideScaleEntryDao(db: KinAptoDatabase): ScaleEntryDao = db.scaleEntryDao()

    @Provides
    fun provideArticleDao(db: KinAptoDatabase): ArticleDao = db.articleDao()

    @Provides
    fun provideExportLogDao(db: KinAptoDatabase): ExportLogDao = db.exportLogDao()

    @Provides
    fun providePatientProfileDao(db: KinAptoDatabase): PatientProfileDao = db.patientProfileDao()

    @Provides
    fun provideGoalDao(db: KinAptoDatabase): GoalDao = db.goalDao()

    @Provides
    fun provideScheduledSessionDao(db: KinAptoDatabase): ScheduledSessionDao = db.scheduledSessionDao()

    @Provides
    fun provideAuditLogDao(db: KinAptoDatabase): AuditLogDao = db.auditLogDao()
}
