// =============================================================
// AFA - Attività Fisica Adattata
// DAO: Operazioni sugli articoli e consigli
// =============================================================
package com.afa.fitadapt.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.afa.fitadapt.data.local.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO per la gestione degli articoli/consigli.
 * Gli articoli sono precaricati e possono essere aggiunti dalla sezione protetta.
 */
@Dao
interface ArticleDao {

    // Ottieni tutti gli articoli
    @Query("SELECT * FROM articles ORDER BY category, title")
    fun getAll(): Flow<List<ArticleEntity>>

    // Ottieni gli articoli di una specifica categoria
    @Query("SELECT * FROM articles WHERE category = :category ORDER BY title")
    fun getByCategory(category: String): Flow<List<ArticleEntity>>

    // Ottieni un articolo per ID
    @Query("SELECT * FROM articles WHERE id = :id")
    suspend fun getById(id: Long): ArticleEntity?

    // Ottieni l'articolo in evidenza della settimana corrente
    @Query("SELECT * FROM articles WHERE isFeatured = 1 LIMIT 1")
    fun getFeaturedArticle(): Flow<ArticleEntity?>

    // Ottieni l'articolo in evidenza per una specifica settimana
    @Query("SELECT * FROM articles WHERE weekNumber = :weekNumber AND year = :year LIMIT 1")
    suspend fun getArticleForWeek(weekNumber: Int, year: Int): ArticleEntity?

    // Cerca articoli per titolo
    @Query("SELECT * FROM articles WHERE title LIKE '%' || :query || '%' ORDER BY title")
    fun searchByTitle(query: String): Flow<List<ArticleEntity>>

    // Inserisci un articolo
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: ArticleEntity): Long

    // Inserisci più articoli in blocco (per il seeding iniziale)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<ArticleEntity>)

    // Aggiorna un articolo
    @Update
    suspend fun update(article: ArticleEntity)

    // Elimina un articolo
    @Delete
    suspend fun delete(article: ArticleEntity)

    // Imposta un articolo come in evidenza (disattiva prima tutti gli altri)
    @Query("UPDATE articles SET isFeatured = 0")
    suspend fun clearAllFeatured()

    @Query("UPDATE articles SET isFeatured = 1 WHERE id = :id")
    suspend fun setFeatured(id: Long)

    // Conta gli articoli totali
    @Query("SELECT COUNT(*) FROM articles")
    suspend fun count(): Int

    // Ottieni tutte le categorie con articoli
    @Query("SELECT DISTINCT category FROM articles ORDER BY category")
    fun getCategories(): Flow<List<String>>
}
