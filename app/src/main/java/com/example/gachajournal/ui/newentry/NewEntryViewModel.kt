package com.example.gachajournal.ui.newentry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.gachajournal.data.JournalRepository
import com.example.gachajournal.data.Reward
import com.example.gachajournal.data.database.JournalEntry
import com.example.gachajournal.data.database.User
import kotlinx.coroutines.launch

class NewEntryViewModel(private val repository: JournalRepository) : ViewModel() {

    private val _reward = MutableLiveData<Reward?>()
    val reward: LiveData<Reward?> = _reward

    val user: LiveData<User?> = repository.user.asLiveData()

    fun saveEntry(entry: JournalEntry) = viewModelScope.launch {
        val rewardResult = repository.insertEntryAndGetReward(entry)
        _reward.postValue(rewardResult)
    }

    fun resetReward() {
        _reward.value = null
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