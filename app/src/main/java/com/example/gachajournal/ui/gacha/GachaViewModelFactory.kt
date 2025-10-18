package com.example.gachajournal.ui.gacha

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gachajournal.data.JournalRepository

class GachaViewModelFactory(private val repository: JournalRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GachaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GachaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}