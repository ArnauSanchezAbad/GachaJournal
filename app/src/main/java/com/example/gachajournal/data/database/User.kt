package com.example.gachajournal.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: Long = 1,
    val gachaPoints: Int = 0,
    // Pity for entry rewards
    val entriesSinceLast4Star: Int = 0,
    val entriesSinceLast5Star: Int = 0,
    // Pity for cosmetic gacha
    val gachaRollsSinceLast4Star: Int = 0,
    val gachaRollsSinceLast5Star: Int = 0
)