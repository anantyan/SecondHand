package id.co.binar.secondhand.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.co.binar.secondhand.data.local.AuthDao
import id.co.binar.secondhand.data.local.SellerDao
import id.co.binar.secondhand.data.remote.*
import id.co.binar.secondhand.database.RoomDatabase
import id.co.binar.secondhand.util.DataStoreManager
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideMoviesDao(roomDB: RoomDatabase): AuthDao {
        return roomDB.authDao()
    }

    @Provides
    fun provideCategoryDao(roomDB: RoomDatabase): SellerDao {
        return roomDB.sellerDao()
    }

    @Singleton
    @Provides
    fun provideAuthApi(@Named("secondhand") retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Singleton
    @Provides
    fun provideBuyerApi(@Named("secondhand") retrofit: Retrofit): BuyerApi {
        return retrofit.create(BuyerApi::class.java)
    }

    @Singleton
    @Provides
    fun provideHistoryApi(@Named("secondhand") retrofit: Retrofit): HistoryApi {
        return retrofit.create(HistoryApi::class.java)
    }

    @Singleton
    @Provides
    fun provideNotificationApi(@Named("secondhand") retrofit: Retrofit): NotificationApi {
        return retrofit.create(NotificationApi::class.java)
    }

    @Singleton
    @Provides
    fun provideSellerApi(@Named("secondhand") retrofit: Retrofit): SellerApi {
        return retrofit.create(SellerApi::class.java)
    }

    @Singleton
    @Provides
    fun provideNotificationFCMApi(@Named("notification") retrofit: Retrofit): NotificationFCMApi {
        return retrofit.create(NotificationFCMApi::class.java)
    }

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStoreManager {
        return DataStoreManager(context)
    }
}