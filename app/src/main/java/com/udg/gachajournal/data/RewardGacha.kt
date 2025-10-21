package com.udg.gachajournal.data

import com.udg.gachajournal.data.database.User
import kotlin.random.Random

data class Reward(val rarity: Int, val points: Int)

object RewardGacha {

    const val PITY_4_STAR = 10
    const val PITY_5_STAR = 50

    private const val BASE_RATE_5_STAR = 0.01 // 1%
    private const val BASE_RATE_4_STAR = 0.10 // 10%

    fun roll(user: User): Reward {
        // Hard Pity System takes precedence
        if (user.entriesSinceLast5Star >= PITY_5_STAR - 1) {
            return Reward(5, 500)
        }
        if (user.entriesSinceLast4Star >= PITY_4_STAR - 1) {
            return Reward(4, 100)
        }

        val roll = Random.nextDouble()
        
        // Fixed rates
        return when {
            roll < BASE_RATE_5_STAR -> Reward(5, 500)
            roll < BASE_RATE_5_STAR + BASE_RATE_4_STAR -> Reward(4, 100)
            else -> Reward(3, 10)
        }
    }
}