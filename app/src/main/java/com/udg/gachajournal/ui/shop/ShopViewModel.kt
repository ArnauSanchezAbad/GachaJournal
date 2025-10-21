package com.udg.gachajournal.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udg.gachajournal.data.JournalRepository
import com.udg.gachajournal.data.database.Cosmetic
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ShopItem(
    val cosmetic: Cosmetic,
    val isOwned: Boolean
)

data class ShopUiState(
    val items: List<ShopItem> = emptyList(),
    val isLoading: Boolean = true
)

class ShopViewModel(private val repository: JournalRepository) : ViewModel() {

    private val _purchaseEvents = MutableSharedFlow<PurchaseResult>()
    val purchaseEvents = _purchaseEvents.asSharedFlow()

    val uiState: StateFlow<ShopUiState> = combine(
        repository.cosmetics, 
        repository.getOwnedCosmetics()
    ) { all, owned ->
        if (all.isEmpty()) {
            ShopUiState(isLoading = true)
        } else {
            val ownedIds = owned.map { it.id }.toSet()
            val shopItems = all.map { ShopItem(it, it.id in ownedIds) }
            ShopUiState(items = shopItems, isLoading = false)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ShopUiState(isLoading = true)
    )

    fun purchaseItem(cosmetic: Cosmetic) {
        viewModelScope.launch {
            val success = repository.purchaseCosmetic(cosmetic)
            if (success) {
                _purchaseEvents.emit(PurchaseResult.SUCCESS)
            } else {
                _purchaseEvents.emit(PurchaseResult.INSUFFICIENT_FUNDS)
            }
        }
    }
}

enum class PurchaseResult {
    SUCCESS,
    INSUFFICIENT_FUNDS
}