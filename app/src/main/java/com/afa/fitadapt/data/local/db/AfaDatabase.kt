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
        GoalEntity::class
    ],
    version = 3,
    exportSchema = true // Abilitato per debugging e generazione file schema
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

    companion object {
        /**
         * Scaffold per le migrazioni future.
         * Quando si cambia lo schema, aggiungere qui una nuova migrazione.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // In questo caso il cambio era nel modo in cui leggiamo la chiave,
                // non nello schema SQL, quindi il corpo può restare vuoto
                // o contenere logica di trasformazione dati se necessaria.
            }
        }
        
        // Esempio per il futuro:
        // val MIGRATION_2_3 = object : Migration(2, 3) { ... }
    }
}
