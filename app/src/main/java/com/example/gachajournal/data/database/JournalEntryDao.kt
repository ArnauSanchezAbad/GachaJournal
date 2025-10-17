package com.example.gachajournal.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface JournalEntryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entry: JournalEntry)
    
    // TODO: Add methods to query entries (e.g., get all, get by date)
}