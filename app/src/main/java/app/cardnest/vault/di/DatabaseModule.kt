package app.cardnest.vault.di

import android.content.Context
import androidx.room.Room
import app.cardnest.vault.data.db.CardNestDatabase
import app.cardnest.vault.data.db.dao.CardDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): CardNestDatabase {
        // Card numbers are encrypted at application level using Android Keystore
        return Room.databaseBuilder(
            context,
            CardNestDatabase::class.java,
            CardNestDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // For v1, safe to use
            .build()
    }

    @Provides
    fun provideCardDao(database: CardNestDatabase): CardDao {
        return database.cardDao()
    }
}
