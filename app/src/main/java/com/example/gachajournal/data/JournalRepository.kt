package com.example.gachajournal.data

import com.example.gachajournal.data.database.JournalEntry
import com.example.gachajournal.data.database.JournalEntryDao
import kotlinx.coroutines.flow.Flow

class JournalRepository(private val journalEntryDao: JournalEntryDao) {

    val allEntries: Flow<List<JournalEntry>> = journalEntryDao.getAllEntries()

    suspend fun insert(entry: JournalEntry) {
        journalEntryDao.insert(entry)
    }
}