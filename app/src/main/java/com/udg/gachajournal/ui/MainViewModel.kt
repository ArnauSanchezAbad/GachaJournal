package com.udg.gachajournal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.udg.gachajournal.data.JournalRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class AppTheme(
    val backgroundColor: String? = null,
    val borderColor: String? = null,
    val fontColor: String? = null
)

class MainViewModel(private val repository: JournalRepository) : ViewModel() {

    val appTheme: StateFlow<AppTheme> = combine(
        repository.user,
        repository.cosmetics
    ) { user, allCosmetics ->
        val cosmeticsById = allCosmetics.associateBy { it.id }
        AppTheme(
            backgroundColor = user?.equippedBackgroundId?.let { cosmeticsById[it]?.assetName },
            borderColor = user?.equippedBorderId?.let { cosmeticsById[it]?.assetName },
            fontColor = user?.equippedFontColorId?.let { cosmeticsById[it]?.assetName }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppTheme()
    )
}

class MainViewModelFactory(private val repository: JournalRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}