package com.example.gachajournal.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [JournalEntry::class, User::class, Cosmetic::class, UserCosmeticCrossRef::class], version = 9, exportSchema = false)
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
                .addCallback(AppDatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback : RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let {
                CoroutineScope(Dispatchers.IO).launch {
                    // Seed database only if it's empty
                    if (it.userDao().getCosmeticsCount() == 0) {
                        seedDatabase(it.userDao())
                    }
                }
            }
        }

        suspend fun seedDatabase(userDao: UserDao) {
            // Add default user with 0 points
            userDao.insertUser(User(id = 1, gachaPoints = 0))

            // Add cosmetics
            val cosmetics = listOf(
                Cosmetic(id=1, name = "Fons per Defecte", description = "El fons original de l'aplicació.", type = "BACKGROUND", rarity = "COMMON", price = 0, assetName = "#FFFFFF"),
                Cosmetic(id=2, name = "Vora per Defecte", description = "La vora original de l'aplicació.", type = "BORDER", rarity = "COMMON", price = 0, assetName = "#000000"),
                Cosmetic(id=3, name = "Font per Defecte", description = "El color de font original.", type = "FONT_COLOR", rarity = "COMMON", price = 0, assetName = "#000000"),
                Cosmetic(id=101, name = "Fons Blau Clar", description = "Un cel serè per a les teves entrades.", type = "BACKGROUND", rarity = "RARE", price = 300, assetName = "#ADD8E6"),
                Cosmetic(id=102, name = "Fons Gris", description = "Un fons neutre i modern.", type = "BACKGROUND", rarity = "COMMON", price = 50, assetName = "#E0E0E0"),
                Cosmetic(id=201, name = "Vora Grisa", description = "Una vora senzilla i elegant.", type = "BORDER", rarity = "COMMON", price = 50, assetName = "#808080"),
                Cosmetic(id=202, name = "Vora Blava", description = "Una vora amb un toc de color fred.", type = "BORDER", rarity = "RARE", price = 200, assetName = "#0000FF"),
                Cosmetic(id=203, name = "Vora Daurada", description = "La vora definitiva.", type = "BORDER", rarity = "LEGENDARY", price = 5000, assetName = "#FFD700"),
                Cosmetic(id=302, name = "Font Vermella", description = "Per a entrades amb passió.", type = "FONT_COLOR", rarity = "RARE", price = 250, assetName = "#FF0000"),
                Cosmetic(id=303, name = "Font Verda", description = "Un toc de natura per al text.", type = "FONT_COLOR", rarity = "RARE", price = 250, assetName = "#008000")
            )
            userDao.insertCosmetics(cosmetics)

            // Unlock default cosmetics for the user
            userDao.addUserCosmetic(UserCosmeticCrossRef(1, 1))
            userDao.addUserCosmetic(UserCosmeticCrossRef(1, 2))
            userDao.addUserCosmetic(UserCosmeticCrossRef(1, 3))
        }
    }
}