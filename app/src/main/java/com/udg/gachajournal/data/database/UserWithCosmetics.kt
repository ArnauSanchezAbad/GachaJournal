package com.udg.gachajournal.data.database

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class UserWithCosmetics(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = UserCosmeticCrossRef::class,
            parentColumn = "userId",
            entityColumn = "cosmeticId"
        )
    )
    val cosmetics: List<Cosmetic>
)