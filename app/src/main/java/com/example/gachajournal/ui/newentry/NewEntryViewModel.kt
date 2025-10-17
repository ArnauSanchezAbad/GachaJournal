package com.example.gachajournal.ui.newentry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gachajournal.data.JournalRepository
import com.example.gachajournal.data.database.JournalEntry
import kotlinx.coroutines.launch

class NewEntryViewModel(private val repository: JournalRepository) : ViewModel() {

    fun insert(entry: JournalEntry) = viewModelScope.launch {
        repository.insert(entry)
    }
}

class NewEntryViewModelFactory(private val repository: JournalRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewEntryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewEntryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}