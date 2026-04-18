// =============================================================
// AFA - Attività Fisica Adattata
// ViewModel: Editor Articolo
// =============================================================
package com.afa.fitadapt.ui.protected_section.management

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afa.fitadapt.data.local.entity.ArticleEntity
import com.afa.fitadapt.data.repository.ArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArticleEditorUiState(
    val id: Long = 0,
    val title: String = "",
    val category: String = "Benessere",
    val summary: String = "",
    val body: String = "",
    val isFeatured: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)

@HiltViewModel
class ArticleEditorViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val articleId: Long = savedStateHandle.get<Long>("articleId") ?: -1L
    private val _uiState = MutableStateFlow(ArticleEditorUiState())
    val uiState = _uiState.asStateFlow()

    init {
        if (articleId != -1L) {
            loadArticle(articleId)
        }
    }

    private fun loadArticle(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            articleRepository.getById(id)?.let { art ->
                _uiState.update { 
                    it.copy(
                        id = art.id,
                        title = art.title,
                        category = art.category,
                        summary = art.summary,
                        body = art.body,
                        isFeatured = art.isFeatured,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateTitle(title: String) = _uiState.update { it.copy(title = title) }
    fun updateCategory(cat: String) = _uiState.update { it.copy(category = cat) }
    fun updateSummary(summary: String) = _uiState.update { it.copy(summary = summary) }
    fun updateBody(body: String) = _uiState.update { it.copy(body = body) }

    fun save() {
        val state = _uiState.value
        val article = ArticleEntity(
            id = if (articleId == -1L) 0 else articleId,
            title = state.title,
            category = state.category,
            summary = state.summary,
            body = state.body,
            isFeatured = state.isFeatured
        )

        viewModelScope.launch {
            if (articleId == -1L) {
                articleRepository.addArticle(article)
            } else {
                articleRepository.updateArticle(article)
            }
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}
