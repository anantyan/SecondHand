package id.co.binar.secondhand.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.co.binar.secondhand.database.RoomDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) : RoomDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            RoomDatabase::class.java,
            "db_second_hand"
        ).fallbackToDestructiveMigration().build()
    }
}