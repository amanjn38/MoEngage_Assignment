package com.example.moengageassignment.di

import android.content.Context
import com.example.moengageassignment.api.ApiService
import com.example.moengageassignment.repository.NewsRepository
import com.example.moengageassignment.roomhelper.AppDatabase
import com.example.moengageassignment.roomhelper.NewsArticleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNewsRepository(apiService: ApiService, newsDao: NewsArticleDao): NewsRepository {
        return NewsRepository(apiService, newsDao)
    }

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return ApiService
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideNewsArticleDao(appDatabase: AppDatabase): NewsArticleDao {
        return appDatabase.newsArticleDao()
    }

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext appContext: Context): Context {
        return appContext
    }
}

