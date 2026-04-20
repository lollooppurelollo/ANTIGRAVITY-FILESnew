// =============================================================
// AFA - Attività Fisica Adattata
// Database Room principale con cifratura SQLCipher
// =============================================================
package com.afa.fitadapt.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.afa.fitadapt.data.local.dao.*
import com.afa.fitadapt.data.local.entity.*

/**
 * Database Room principale dell'app AFA.
 *
 * Contiene 11 tabelle per gestire:
 * - Libreria esercizi
 * - Schede di allenamento e relazione con esercizi
 * - Sessioni e dettaglio esercizi completati
 * - Diario libero
 * - Scale rapide (astenia, dolore, dispnea)
 * - Articoli/consigli
 * - Log degli export
 * - Profilo paziente
 * - Obiettivi
 *
 * Il database è CIFRATO con SQLCipher.
 * La chiave di cifratura è gestita da CryptoManager e protetta
 * dal Keystore Android. Vedi DatabaseModule per i dettagli.
 *
 * Versione 3: Rimosso campo ridondante in PatientProfileEntity e allineamento schema.
 */
@Database(
    entities = [
        ExerciseEntity::class,
        TrainingCardEntity::class,
        CardExerciseEntity::class,
        SessionEntity::class,
        SessionExerciseEntity::class,
        DiaryEntryEntity::class,
        ScaleEntryEntity::class,
        ArticleEntity::class,
        ExportLogEntity::class,
        PatientProfileEntity::class,
        GoalEntity::class,
        ScheduledSessionEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AfaDatabase : RoomDatabase() {

    // ── DAO per ogni tabella ──
    abstract fun exerciseDao(): ExerciseDao
    abstract fun trainingCardDao(): TrainingCardDao
    abstract fun sessionDao(): SessionDao
    abstract fun diaryDao(): DiaryDao
    abstract fun scaleEntryDao(): ScaleEntryDao
    abstract fun articleDao(): ArticleDao
    abstract fun exportLogDao(): ExportLogDao
    abstract fun patientProfileDao(): PatientProfileDao
    abstract fun goalDao(): GoalDao
    abstract fun scheduledSessionDao(): ScheduledSessionDao

    companion object {
        /**
         * Scaffold per le migrazioni future.
         * Quando si cambia lo schema, aggiungere qui una nuova migrazione.
         */
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Aggiunta campi silverValue e goldValue alla tabella goals
                db.execSQL("ALTER TABLE goals ADD COLUMN silverValue REAL DEFAULT NULL")
                db.execSQL("ALTER TABLE goals ADD COLUMN goldValue REAL DEFAULT NULL")
            }
        }
    }
}
