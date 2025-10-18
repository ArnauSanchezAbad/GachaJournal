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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE id = 1")
    fun getUser(): Flow<User?>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): User?

    @Query("SELECT COUNT(*) FROM cosmetics")
    fun getCosmeticsCount(): Int // Synchronous check

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCosmetics(cosmetics: List<Cosmetic>)

    @Query("SELECT * FROM cosmetics")
    fun getAllCosmetics(): Flow<List<Cosmetic>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUserCosmetic(crossRef: UserCosmeticCrossRef)

    @Transaction
    @Query("SELECT * FROM users WHERE id = 1")
    fun getUserWithCosmetics(): Flow<UserWithCosmetics?>
}