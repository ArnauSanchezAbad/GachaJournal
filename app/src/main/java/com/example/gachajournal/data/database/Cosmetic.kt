package com.example.gachajournal.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cosmetics")
data class Cosmetic(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val type: String, // e.g., "BORDER", "BACKGROUND", "ANIMATION"
    val rarity: String, // e.g., "COMMON", "RARE", "EPIC", "LEGENDARY"
    val price: Int, // Price in the direct-buy shop
    val assetName: String // Name of the resource, e.g., "border_gold"
)