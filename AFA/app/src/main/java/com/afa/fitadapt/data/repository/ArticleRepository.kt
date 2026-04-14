// =============================================================
// AFA - Attività Fisica Adattata
// Repository: Articoli e consigli
// =============================================================
package com.afa.fitadapt.data.repository

import com.afa.fitadapt.data.local.dao.ArticleDao
import com.afa.fitadapt.data.local.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository per la gestione degli articoli e consigli.
 *
 * Gli articoli sono precaricati (vedi DatabaseSeeder) e possono
 * essere aggiunti dalla sezione protetta.
 *
 * Logica speciale:
 * - Un articolo può essere "in evidenza" per la settimana corrente
 * - Se i punteggi delle scale rapide sono alti, gli articoli pertinenti
 *   hanno priorità (gestito dal ViewModel)
 */
@Singleton
class ArticleRepository @Inject constructor(
    private val articleDao: ArticleDao
) {

    /** Tutti gli articoli */
    fun getAllArticles(): Flow<List<ArticleEntity>> =
        articleDao.getAll()

    /** Articoli per categoria */
    @Suppress("unused") // Usata dal ArticleViewModel per filtro per categoria
    fun getByCategory(category: String): Flow<List<ArticleEntity>> =
        articleDao.getByCategory(category)

    /** L'articolo in evidenza della settimana */
    fun getFeaturedArticle(): Flow<ArticleEntity?> =
        articleDao.getFeaturedArticle()

    /** Un articolo per ID */
    suspend fun getById(id: Long): ArticleEntity? =
        articleDao.getById(id)

    /** Cerca articoli per titolo */
    @Suppress("unused") // Usata dalla barra di ricerca nella schermata articoli
    fun searchByTitle(query: String): Flow<List<ArticleEntity>> =
        articleDao.searchByTitle(query)

    /** Categorie disponibili */
    fun getCategories(): Flow<List<String>> =
        articleDao.getCategories()

    /** Aggiungi un nuovo articolo (dalla sezione protetta) */
    @Suppress("unused") // Chiamata dalla sezione protetta - editor articoli (Tranche 5)
    suspend fun addArticle(article: ArticleEntity): Long =
        articleDao.insert(article)

    /** Aggiorna un articolo */
    @Suppress("unused") // Chiamata dalla sezione protetta - modifica articolo
    suspend fun updateArticle(article: ArticleEntity) =
        articleDao.update(article)

    /** Elimina un articolo */
    @Suppress("unused") // Chiamata dalla sezione protetta - elimina articolo
    suspend fun deleteArticle(article: ArticleEntity) =
        articleDao.delete(article)

    /** Imposta un articolo come in evidenza */
    @Suppress("unused") // Chiamata dalla sezione protetta per articolo in evidenza
    suspend fun setFeatured(articleId: Long) {
        articleDao.clearAllFeatured()
        articleDao.setFeatured(articleId)
    }

    /** Verifica se ci sono articoli (per il seeding) */
    @Suppress("unused") // Chiamata dal DatabaseSeeder al primo avvio
    suspend fun isEmpty(): Boolean =
        articleDao.count() == 0
}
