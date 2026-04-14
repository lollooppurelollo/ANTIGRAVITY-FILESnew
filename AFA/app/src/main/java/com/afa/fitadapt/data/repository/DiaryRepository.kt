// =============================================================
// AFA - Attività Fisica Adattata
// Repository: Diario libero e scale rapide
// =============================================================
package com.afa.fitadapt.data.repository

import com.afa.fitadapt.data.local.dao.DiaryDao
import com.afa.fitadapt.data.local.dao.ScaleEntryDao
import com.afa.fitadapt.data.local.entity.DiaryEntryEntity
import com.afa.fitadapt.data.local.entity.ScaleEntryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository che unisce diario libero e scale rapide.
 * Entrambe le funzionalità sono accessibili dalla stessa schermata
 * e condividono il concetto di "registrazione giornaliera".
 */
@Singleton
class DiaryRepository @Inject constructor(
    private val diaryDao: DiaryDao,
    private val scaleEntryDao: ScaleEntryDao
) {

    // ══════════════════════════════════════════════════════════
    // DIARIO LIBERO
    // ══════════════════════════════════════════════════════════

    /** Tutte le voci del diario dalla più recente */
    fun getAllDiaryEntries(): Flow<List<DiaryEntryEntity>> =
        diaryDao.getAll()

    /** Voci del diario per un giorno specifico */
    @Suppress("unused") // Filtro per giorno, usata dalla vista calendario
    fun getDiaryEntriesForDay(startOfDay: Long, endOfDay: Long): Flow<List<DiaryEntryEntity>> =
        diaryDao.getEntriesForDay(startOfDay, endOfDay)

    /** Voci del diario in un intervallo di date */
    @Suppress("unused") // Filtro per periodo, usata per export e grafici
    fun getDiaryEntriesInRange(fromDate: Long, toDate: Long): Flow<List<DiaryEntryEntity>> =
        diaryDao.getEntriesInRange(fromDate, toDate)

    /** Aggiungi una voce al diario */
    suspend fun addDiaryEntry(entry: DiaryEntryEntity): Long =
        diaryDao.insert(entry)

    /** Aggiorna una voce del diario */
    @Suppress("unused") // Usata per modifica voce del diario
    suspend fun updateDiaryEntry(entry: DiaryEntryEntity) =
        diaryDao.update(entry)

    /** Elimina una voce del diario */
    suspend fun deleteDiaryEntry(entry: DiaryEntryEntity) =
        diaryDao.delete(entry)

    // ══════════════════════════════════════════════════════════
    // SCALE RAPIDE (0-10)
    // ══════════════════════════════════════════════════════════

    /** Tutte le rilevazioni */
    fun getAllScaleEntries(): Flow<List<ScaleEntryEntity>> =
        scaleEntryDao.getAll()

    /** Ultime N rilevazioni (per trend rapido) */
    fun getLatestScaleEntries(limit: Int): Flow<List<ScaleEntryEntity>> =
        scaleEntryDao.getLatestEntries(limit)

    /** Rilevazioni in un intervallo di date (per grafici) */
    fun getScaleEntriesInRange(fromDate: Long, toDate: Long): Flow<List<ScaleEntryEntity>> =
        scaleEntryDao.getEntriesInRange(fromDate, toDate)

    /** Ultima rilevazione */
    @Suppress("unused") // Usata dalla HomeScreen per il widget ultima rilevazione
    suspend fun getLatestScaleEntry(): ScaleEntryEntity? =
        scaleEntryDao.getLatest()

    /** Registra una nuova rilevazione scale */
    suspend fun addScaleEntry(entry: ScaleEntryEntity): Long =
        scaleEntryDao.insert(entry)

    /** Aggiorna una rilevazione */
    @Suppress("unused") // Usata per correzione rilevazione inserita per errore
    suspend fun updateScaleEntry(entry: ScaleEntryEntity) =
        scaleEntryDao.update(entry)

    // ══════════════════════════════════════════════════════════
    // MEDIE (per prioritizzazione articoli)
    // ══════════════════════════════════════════════════════════

    /**
     * Ottieni le medie dei punteggi nell'ultimo periodo.
     * Usate per dare priorità agli articoli pertinenti quando i punteggi sono alti.
     *
     * @param sinceDate timestamp da cui calcolare le medie
     * @return mappa scala->media (null se non ci sono dati)
     */
    @Suppress("unused") // Usata da ArticleRepository per prioritizzazione articoli
    suspend fun getRecentAverages(sinceDate: Long): Map<String, Float?> {
        return mapOf(
            "asthenia" to scaleEntryDao.averageAstheniaSince(sinceDate),
            "pain" to scaleEntryDao.averagePainSince(sinceDate),
            "restDyspnea" to scaleEntryDao.averageRestDyspneaSince(sinceDate),
            "exertionDyspnea" to scaleEntryDao.averageExertionDyspneaSince(sinceDate)
        )
    }
}
