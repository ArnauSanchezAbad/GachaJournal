package com.example.gachajournal.data

import com.example.gachajournal.data.database.Cosmetic
import com.example.gachajournal.data.database.User
import kotlin.random.Random

object CosmeticGacha {

    const val PITY_EPIC = 10
    const val PITY_LEGENDARY = 90

    private const val RATE_LEGENDARY = 0.01 // 1%
    private const val RATE_EPIC = 0.05       // 5%
    private const val RATE_RARE = 0.14        // 14%

    fun roll(user: User, cosmetics: List<Cosmetic>, ownedCosmeticIds: List<Long>): Cosmetic? {
        val availableCosmetics = cosmetics.filter { !ownedCosmeticIds.contains(it.id) }
        if (availableCosmetics.isEmpty()) return null

        // Hard Pity System
        if (user.gachaRollsSinceLast5Star >= PITY_LEGENDARY - 1) {
            return availableCosmetics.filter { it.rarity == "LEGENDARY" }.randomOrNull() ?: availableCosmetics.random()
        }
        if (user.gachaRollsSinceLast4Star >= PITY_EPIC - 1) {
            return availableCosmetics.filter { it.rarity == "EPIC" }.randomOrNull() ?: availableCosmetics.random()
        }

        val roll = Random.nextDouble()

        val rarity = when {
            roll < RATE_LEGENDARY -> "LEGENDARY"
            roll < RATE_LEGENDARY + RATE_EPIC -> "EPIC"
            roll < RATE_LEGENDARY + RATE_EPIC + RATE_RARE -> "RARE"
            else -> "COMMON"
        }

        val possiblePrizes = availableCosmetics.filter { it.rarity == rarity }
        
        return possiblePrizes.ifEmpty { availableCosmetics.shuffled() }.random()
    }
}