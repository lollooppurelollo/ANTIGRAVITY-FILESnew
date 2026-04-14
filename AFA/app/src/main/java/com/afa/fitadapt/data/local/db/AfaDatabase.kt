// =============================================================
// AFA - Attività Fisica Adattata
// Database Room principale con cifratura SQLCipher
// =============================================================
package com.afa.fitadapt.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.afa.fitadapt.data.local.dao.ArticleDao
import com.afa.fitadapt.data.local.dao.DiaryDao
import com.afa.fitadapt.data.local.dao.ExerciseDao
import com.afa.fitadapt.data.local.dao.ExportLogDao
import com.afa.fitadapt.data.local.dao.GoalDao
import com.afa.fitadapt.data.local.dao.PatientProfileDao
import com.afa.fitadapt.data.local.dao.ScaleEntryDao
import com.afa.fitadapt.data.local.dao.SessionDao
import com.afa.fitadapt.data.local.dao.TrainingCardDao
import com.afa.fitadapt.data.local.entity.ArticleEntity
import com.afa.fitadapt.data.local.entity.CardExerciseEntity
import com.afa.fitadapt.data.local.entity.DiaryEntryEntity
import com.afa.fitadapt.data.local.entity.ExerciseEntity
import com.afa.fitadapt.data.local.entity.ExportLogEntity
import com.afa.fitadapt.data.local.entity.GoalEntity
import com.afa.fitadapt.data.local.entity.PatientProfileEntity
import com.afa.fitadapt.data.local.entity.ScaleEntryEntity
import com.afa.fitadapt.data.local.entity.SessionEntity
import com.afa.fitadapt.data.local.entity.SessionExerciseEntity
import com.afa.fitadapt.data.local.entity.TrainingCardEntity

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
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AfaDatabase : RoomDatabase() {

    // ── DAO per ogni tabella ──

    /** Esercizi nella libreria precaricata */
    abstract fun exerciseDao(): ExerciseDao

    /** Schede di allenamento e relazione con esercizi */
    abstract fun trainingCardDao(): TrainingCardDao

    /** Sessioni di allenamento registrate */
    abstract fun sessionDao(): SessionDao

    /** Voci del diario libero */
    abstract fun diaryDao(): DiaryDao

    /** Punteggi delle scale rapide */
    abstract fun scaleEntryDao(): ScaleEntryDao

    /** Articoli e consigli */
    abstract fun articleDao(): ArticleDao

    /** Log degli export effettuati */
    abstract fun exportLogDao(): ExportLogDao

    /** Profilo locale della paziente */
    abstract fun patientProfileDao(): PatientProfileDao

    /** Obiettivi configurabili */
    abstract fun goalDao(): GoalDao
}
