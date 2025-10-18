package com.example.gachajournal.data

import com.example.gachajournal.data.database.JournalEntry
import com.example.gachajournal.data.database.JournalEntryDao
import com.example.gachajournal.data.database.User
import com.example.gachajournal.data.database.UserDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class JournalRepository(private val journalEntryDao: JournalEntryDao, private val userDao: UserDao) {

    val allEntries: Flow<List<JournalEntry>> = journalEntryDao.getAllEntries()
    val user: Flow<User?> = userDao.getUser()

    suspend fun insertEntryAndGetReward(entry: JournalEntry): Reward {
        var user = userDao.getUser().first()
        // If user doesn't exist, create a default one on the fly
        if (user == null) {
            val defaultUser = User(id = 1, gachaPoints = 0)
            userDao.insertUser(defaultUser)
            user = defaultUser
        }

        val reward = RewardGacha.roll(user)

        val newUser = when (reward.rarity) {
            5 -> user.copy(
                gachaPoints = user.gachaPoints + reward.points,
                entriesSinceLast5Star = 0,
                entriesSinceLast4Star = 0 // Also reset 4-star pity
            )
            4 -> user.copy(
                gachaPoints = user.gachaPoints + reward.points,
                entriesSinceLast4Star = 0,
                entriesSinceLast5Star = user.entriesSinceLast5Star + 1
            )
            else -> user.copy(
                gachaPoints = user.gachaPoints + reward.points,
                entriesSinceLast4Star = user.entriesSinceLast4Star + 1,
                entriesSinceLast5Star = user.entriesSinceLast5Star + 1
            )
        }

        userDao.updateUser(newUser)

        journalEntryDao.insert(entry)

        return reward
    }

    suspend fun delete(entry: JournalEntry) {
        journalEntryDao.delete(entry)
    }
}