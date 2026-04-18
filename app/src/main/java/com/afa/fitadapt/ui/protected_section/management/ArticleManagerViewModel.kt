// =============================================================
// AFA - Attività Fisica Adattata
// ViewModel: Gestione Articoli
// =============================================================
package com.afa.fitadapt.ui.protected_section.management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afa.fitadapt.data.local.entity.ArticleEntity
import com.afa.fitadapt.data.repository.ArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArticleManagerUiState(
    val articles: List<ArticleEntity> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class ArticleManagerViewModel @Inject constructor(
    private val articleRepository: ArticleRepository
) : ViewModel() {

    val uiState: StateFlow<ArticleManagerUiState> = articleRepository.getAllArticles()
        .map { articles -> ArticleManagerUiState(articles = articles) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ArticleManagerUiState(isLoading = true))

    fun deleteArticle(article: ArticleEntity) {
        viewModelScope.launch {
            articleRepository.deleteArticle(article)
        }
    }

    fun setFeatured(id: Long) {
        viewModelScope.launch {
            articleRepository.setFeatured(id)
        }
    }
}
