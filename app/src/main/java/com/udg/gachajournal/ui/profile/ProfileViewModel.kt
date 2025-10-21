package com.udg.gachajournal.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udg.gachajournal.data.JournalRepository
import com.udg.gachajournal.data.database.Cosmetic
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileOption(
    val cosmetic: Cosmetic,
    val isOwned: Boolean,
    val isEquipped: Boolean
)

data class ProfileUiState(
    val optionsByType: Map<String, List<ProfileOption>> = emptyMap(),
    val isLoading: Boolean = true
)

class ProfileViewModel(private val repository: JournalRepository) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        repository.cosmetics,
        repository.getOwnedCosmetics(),
        repository.user
    ) { allCosmetics, ownedCosmetics, user ->
        if (allCosmetics.isEmpty()) {
            ProfileUiState(isLoading = true)
        } else {
            val ownedIds = ownedCosmetics.map { it.id }.toSet()
            val equippedIds = setOfNotNull(
                user?.equippedBackgroundId,
                user?.equippedBorderId,
                user?.equippedFontColorId
            )

            val options = allCosmetics.map { cosmetic ->
                ProfileOption(
                    cosmetic = cosmetic,
                    isOwned = cosmetic.id in ownedIds,
                    isEquipped = cosmetic.id in equippedIds
                )
            }
            ProfileUiState(optionsByType = options.groupBy { it.cosmetic.type }, isLoading = false)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState(isLoading = true)
    )

    fun equipCosmetic(cosmetic: Cosmetic) {
        viewModelScope.launch {
            repository.equipCosmetic(cosmetic)
        }
    }
}