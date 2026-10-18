package app.cardnest.vault.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import app.cardnest.vault.data.db.dao.CardDao
import app.cardnest.vault.data.db.entities.CardEntity

@Database(
    entities = [CardEntity::class],
    version = 1,
    exportSchema = true
)
abstract class CardNestDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao

    companion object {
        const val DATABASE_NAME = "cardnest_db"
    }
}
