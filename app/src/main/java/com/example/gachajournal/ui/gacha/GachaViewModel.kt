package com.example.gachajournal.ui.gacha

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gachajournal.data.JournalRepository
import com.example.gachajournal.data.database.Cosmetic
import com.example.gachajournal.data.database.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

const val SINGLE_ROLL_COST = 100

data class GachaUiState(
    val user: User? = null,
    val isRolling: Boolean = false,
    val result: Cosmetic? = null,
    val showResult: Boolean = false
)

class GachaViewModel(private val repository: JournalRepository) : ViewModel() {

    private val _isRolling = MutableStateFlow(false)
    private val _result = MutableStateFlow<Cosmetic?>(null)
    private val _showResult = MutableStateFlow(false)

    val uiState: StateFlow<GachaUiState> = combine(
        repository.user,
        _isRolling,
        _result,
        _showResult
    ) { user, isRolling, result, showResult ->
        GachaUiState(user, isRolling, result, showResult)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GachaUiState()
    )

    fun performSingleRoll() {
        viewModelScope.launch {
            _isRolling.value = true
            _showResult.value = false
            _result.value = null

            delay(1500) // Animation delay

            val rollResult = repository.performGachaRoll(cost = SINGLE_ROLL_COST)
            _result.value = rollResult
            _isRolling.value = false
            _showResult.value = true
        }
    }

    fun performMultiRoll() {
        viewModelScope.launch {
            _isRolling.value = true
            _showResult.value = false
            _result.value = null

            var lastResult: Cosmetic? = null
            for (i in 1..10) {
                lastResult = repository.performGachaRoll(cost = SINGLE_ROLL_COST)
                delay(200) // Small delay between each roll in the background
            }
            
            _result.value = lastResult
            _isRolling.value = false
            _showResult.value = true
        }
    }
}

class GachaViewModelFactory(private val repository: JournalRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GachaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GachaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}