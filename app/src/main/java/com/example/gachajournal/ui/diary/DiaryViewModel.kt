package com.example.gachajournal.ui.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.gachajournal.data.JournalRepository

class DiaryViewModel(private val repository: JournalRepository) : ViewModel() {

    val allEntries = repository.allEntries.asLiveData()

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