package com.example.gachajournal.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: Long = 1,
    val gachaPoints: Int = 0,
    val entriesSinceLast4Star: Int = 0,
    val entriesSinceLast5Star: Int = 0
)