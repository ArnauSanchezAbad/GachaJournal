package com.udg.gachajournal.ui.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.udg.gachajournal.data.JournalRepository
import com.udg.gachajournal.data.database.JournalEntry
import kotlinx.coroutines.launch

class DiaryViewModel(private val repository: JournalRepository) : ViewModel() {

    val allEntries = repository.allEntries.asLiveData()

    fun delete(entry: JournalEntry) = viewModelScope.launch {
        repository.delete(entry)
    }

}

class DiaryViewModelFactory(private val repository: JournalRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiaryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DiaryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}