package com.example.gachajournal.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // --- User --- //
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET gachaPoints = :points WHERE id = :userId")
    suspend fun updateUserPoints(points: Int, userId: Long = 1)

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUser(userId: Long = 1): Flow<User?>

    // --- Cosmetics --- //
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCosmetics(cosmetics: List<Cosmetic>)

    @Query("SELECT * FROM cosmetics")
    fun getAllCosmetics(): Flow<List<Cosmetic>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUserCosmetic(crossRef: UserCosmeticCrossRef)

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserWithCosmetics(userId: Long = 1): Flow<UserWithCosmetics?>
}
