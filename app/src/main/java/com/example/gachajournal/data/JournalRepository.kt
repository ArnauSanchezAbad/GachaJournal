package com.example.gachajournal.data

import com.example.gachajournal.data.database.JournalEntry
import com.example.gachajournal.data.database.JournalEntryDao

class JournalRepository(private val journalEntryDao: JournalEntryDao) {

    suspend fun insert(entry: JournalEntry) {
        journalEntryDao.insert(entry)
    }

    // Més endavant, aquí afegirem funcions per obtenir les entrades
}