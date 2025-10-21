package com.udg.gachajournal.data.database

import androidx.room.Entity

@Entity(primaryKeys = ["userId", "cosmeticId"])
data class UserCosmeticCrossRef(
    val userId: Long,
    val cosmeticId: Long
)