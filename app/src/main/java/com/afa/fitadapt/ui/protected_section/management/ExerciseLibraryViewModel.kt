// =============================================================
// AFA - Attività Fisica Adattata
// ViewModel: Gestione Libreria Esercizi
// =============================================================
package com.afa.fitadapt.ui.protected_section.management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afa.fitadapt.data.local.entity.ExerciseEntity
import com.afa.fitadapt.data.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExerciseLibraryUiState(
    val exercises: List<ExerciseEntity> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

@HiltViewModel
class ExerciseLibraryViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<ExerciseLibraryUiState> = combine(
        exerciseRepository.getAllActive(),
        exerciseRepository.getActiveCategories(),
        _selectedCategory,
        _searchQuery
    ) { exercises, categories, selectedCat, query ->
        val filtered = exercises.filter { exercise ->
            (selectedCat == null || exercise.category == selectedCat) &&
            (query.isBlank() || exercise.name.contains(query, ignoreCase = true))
        }
        ExerciseLibraryUiState(
            exercises = filtered,
            categories = categories,
            selectedCategory = selectedCat,
            searchQuery = query
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ExerciseLibraryUiState(isLoading = true))

    fun setCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun deleteExercise(id: Long) {
        viewModelScope.launch {
            exerciseRepository.deactivateExercise(id)
        }
    }
}
