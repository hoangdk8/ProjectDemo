package com.example.projectdemo.di

import android.app.Application
import androidx.room.Room
import com.example.projectdemo.data.local.AppDatabase
import com.example.projectdemo.data.local.MusicDao
import com.example.projectdemo.data.local.RecentSearchDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application, callback: AppDatabase.Callback): AppDatabase{
        return Room.databaseBuilder(application, AppDatabase::class.java, "app_database")
            .allowMainThreadQueries().fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideArticleDao(db: AppDatabase): MusicDao{
        return db.musicDao()
    }
    @Provides
    fun provideRecentDao(db: AppDatabase): RecentSearchDao{
        return db.recentSearchDao()
    }
}