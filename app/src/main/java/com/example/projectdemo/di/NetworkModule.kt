package com.example.projectdemo.di

import com.example.projectdemo.data.repository.CategoryRepository
import com.example.projectdemo.domain.ApiService
import com.example.projectdemo.data.dataclass.BASE_URL_API
import com.example.projectdemo.data.repository.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(
                BASE_URL_API
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun providesHomeRepository(apiService: ApiService) = HomeRepository(apiService)

    @Singleton
    @Provides
    fun providesCategoryRepository(apiService: ApiService) = CategoryRepository(apiService)
}
