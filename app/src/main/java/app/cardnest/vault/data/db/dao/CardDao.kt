package app.cardnest.vault.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import app.cardnest.vault.data.db.entities.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Query("SELECT * FROM cards WHERE isArchived = 0 ORDER BY lastAccessedAt DESC")
    fun getAllCards(): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE isArchived = 1 ORDER BY modifiedAt DESC")
    fun getArchivedCards(): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getCardById(id: String): CardEntity?

    @Query("""
        SELECT * FROM cards WHERE isArchived = 0 AND (
            nickname LIKE '%' || :query || '%' OR
            issuer LIKE '%' || :query || '%' OR
            cardholderName LIKE '%' || :query || '%'
        ) ORDER BY lastAccessedAt DESC
    """)
    fun searchCards(query: String): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE isFavorite = 1 AND isArchived = 0 ORDER BY lastAccessedAt DESC")
    fun getFavoriteCards(): Flow<List<CardEntity>>

    @Query("SELECT COUNT(*) FROM cards WHERE isArchived = 0")
    fun getCardCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CardEntity)

    @Update
    suspend fun updateCard(card: CardEntity)

    @Query("DELETE FROM cards WHERE id = :id")
    suspend fun deleteCard(id: String)

    @Query("DELETE FROM cards")
    suspend fun deleteAllCards()

    @Query("UPDATE cards SET lastAccessedAt = :timestamp WHERE id = :id")
    suspend fun updateLastAccessed(id: String, timestamp: Long)

    @Query("SELECT * FROM cards")
    suspend fun getAllCardsOneShot(): List<CardEntity>
}
