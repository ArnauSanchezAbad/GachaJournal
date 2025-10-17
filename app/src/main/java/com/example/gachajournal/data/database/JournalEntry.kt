package com.example.gachajournal.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    val game: String,
    val imageUri: String?,
    val description: String
)