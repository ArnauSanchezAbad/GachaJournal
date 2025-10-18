package com.example.gachajournal.data

import com.example.gachajournal.data.database.Cosmetic
import com.example.gachajournal.data.database.JournalEntry
import com.example.gachajournal.data.database.JournalEntryDao
import com.example.gachajournal.data.database.User
import com.example.gachajournal.data.database.UserCosmeticCrossRef
import com.example.gachajournal.data.database.UserDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class JournalRepository(private val journalEntryDao: JournalEntryDao, private val userDao: UserDao) {

    val allEntries: Flow<List<JournalEntry>> = journalEntryDao.getAllEntries()
    val user: Flow<User?> = userDao.getUser()
    val cosmetics: Flow<List<Cosmetic>> = userDao.getAllCosmetics()
    val userWithCosmetics = userDao.getUserWithCosmetics()

    fun getOwnedCosmetics(): Flow<List<Cosmetic>> {
        return userWithCosmetics.map { it?.cosmetics ?: emptyList() }
    }

    suspend fun equipCosmetic(cosmetic: Cosmetic, userId: Long = 1) {
        val user = userDao.getUserById(userId) ?: return
        val updatedUser = when (cosmetic.type) {
            "BACKGROUND" -> user.copy(equippedBackgroundId = if (cosmetic.id == 1L) null else cosmetic.id)
            "BORDER" -> user.copy(equippedBorderId = if (cosmetic.id == 2L) null else cosmetic.id)
            "FONT_COLOR" -> user.copy(equippedFontColorId = if (cosmetic.id == 3L) null else cosmetic.id)
            else -> user
        }
        userDao.updateUser(updatedUser)
    }

    suspend fun purchaseCosmetic(cosmetic: Cosmetic, userId: Long = 1): Boolean {
        val user = userDao.getUserById(userId) ?: return false
        if (user.gachaPoints >= cosmetic.price) {
            val updatedUser = user.copy(gachaPoints = user.gachaPoints - cosmetic.price)
            userDao.updateUser(updatedUser)
            userDao.addUserCosmetic(UserCosmeticCrossRef(userId, cosmetic.id))
            return true
        }
        return false
    }

    suspend fun insertEntryAndGetReward(entry: JournalEntry): Reward {
        var user = userDao.getUser().first()
        if (user == null) {
            val defaultUser = User(id = 1, gachaPoints = 0)
            userDao.insertUser(defaultUser)
            user = defaultUser
        }

        val reward = RewardGacha.roll(user)

        val newUser = when (reward.rarity) {
            5 -> user.copy(gachaPoints = user.gachaPoints + reward.points, entriesSinceLast5Star = 0, entriesSinceLast4Star = 0)
            4 -> user.copy(gachaPoints = user.gachaPoints + reward.points, entriesSinceLast4Star = 0, entriesSinceLast5Star = user.entriesSinceLast5Star + 1)
            else -> user.copy(gachaPoints = user.gachaPoints + reward.points, entriesSinceLast4Star = user.entriesSinceLast4Star + 1, entriesSinceLast5Star = user.entriesSinceLast5Star + 1)
        }

        userDao.updateUser(newUser)
        journalEntryDao.insert(entry)
        return reward
    }

    suspend fun performGachaRoll(userId: Long = 1, cost: Int): Cosmetic? {
        val user = userDao.getUserById(userId) ?: return null
        if (user.gachaPoints < cost) return null

        val allCosmetics = cosmetics.first()
        val ownedCosmetics = userDao.getUserWithCosmetics().first()?.cosmetics ?: emptyList()

        val prize = CosmeticGacha.roll(user, allCosmetics, ownedCosmetics.map { it.id })

        if (prize != null) {
            val newUser = when (prize.rarity) {
                "LEGENDARY" -> user.copy(gachaPoints = user.gachaPoints - cost, gachaRollsSinceLast5Star = 0, gachaRollsSinceLast4Star = 0)
                "EPIC" -> user.copy(gachaPoints = user.gachaPoints - cost, gachaRollsSinceLast4Star = 0, gachaRollsSinceLast5Star = user.gachaRollsSinceLast5Star + 1)
                else -> user.copy(gachaPoints = user.gachaPoints - cost, gachaRollsSinceLast4Star = user.gachaRollsSinceLast4Star + 1, gachaRollsSinceLast5Star = user.gachaRollsSinceLast5Star + 1)
            }
            userDao.updateUser(newUser)
            userDao.addUserCosmetic(UserCosmeticCrossRef(userId, prize.id))
        }

        return prize
    }

    suspend fun delete(entry: JournalEntry) {
        journalEntryDao.delete(entry)
    }
}