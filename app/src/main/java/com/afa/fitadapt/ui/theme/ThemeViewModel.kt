package com.afa.fitadapt.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afa.fitadapt.data.local.datastore.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    val currentTheme: StateFlow<ThemeOption> = userPreferences.appTheme
        .map { ThemeOption.fromOrdinal(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeOption.LIGHT_BLUE
        )

    fun setTheme(theme: ThemeOption) {
        viewModelScope.launch {
            userPreferences.setAppTheme(theme.ordinal)
        }
    }
}
