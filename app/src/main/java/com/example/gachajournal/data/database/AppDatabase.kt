package com.example.gachajournal.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [JournalEntry::class, User::class, Cosmetic::class, UserCosmeticCrossRef::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun journalEntryDao(): JournalEntryDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gacha_journal_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(AppDatabaseCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let {
                val scope = CoroutineScope(Dispatchers.IO)
                scope.launch {
                    seedDatabase(it.userDao())
                }
            }
        }

        suspend fun seedDatabase(userDao: UserDao) {
            // Add default user
            userDao.insertUser(User(id = 1, gachaPoints = 1000)) // Start with some points

            // Add cosmetics
            val cosmetics = listOf(
                // Borders (Common)
                Cosmetic(name = "Vora Grisa", description = "Una vora senzilla i elegant.", type = "BORDER", rarity = "COMMON", price = 50, assetName = "border_gray"),
                Cosmetic(name = "Vora Blanca", description = "Una vora neta i clàssica.", type = "BORDER", rarity = "COMMON", price = 50, assetName = "border_white"),
                
                // Borders (Rare)
                Cosmetic(name = "Vora Blava", description = "Una vora amb un toc de color fred.", type = "BORDER", rarity = "RARE", price = 200, assetName = "border_blue"),
                Cosmetic(name = "Vora Verda", description = "Una vora que recorda la natura.", type = "BORDER", rarity = "RARE", price = 200, assetName = "border_green"),

                // Borders (Epic)
                Cosmetic(name = "Vora Èpica Porpra", description = "Una vora que irradia poder.", type = "BORDER", rarity = "EPIC", price = 1000, assetName = "border_purple"),
                
                // Borders (Legendary)
                Cosmetic(name = "Vora Daurada Llegndària", description = "La vora definitiva, per als autèntics mestres del gacha.", type = "BORDER", rarity = "LEGENDARY", price = 5000, assetName = "border_gold"),

                // Backgrounds (Rare)
                Cosmetic(name = "Fons de Núvols", description = "Un cel serè per a les teves entrades.", type = "BACKGROUND", rarity = "RARE", price = 300, assetName = "bg_clouds"),

                // Animations (Legendary)
                Cosmetic(name = "Animació d'Estrelles Fugaces", description = "Partícules brillants suren per la pantalla.", type = "ANIMATION", rarity = "LEGENDARY", price = 10000, assetName = "anim_shooting_stars")
            )
            userDao.insertCosmetics(cosmetics)
        }
    }
}