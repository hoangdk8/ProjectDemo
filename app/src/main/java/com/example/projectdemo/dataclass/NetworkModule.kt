package com.example.projectdemo.dataclass

import androidx.lifecycle.ViewModelProvider
import com.example.projectdemo.MyApplication
import com.example.projectdemo.explore.viewmodel.ExploreViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ViewModelScoped
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
            .baseUrl("https://dogonoithatxinh.com/minringtone/rest/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): DefaultRings {
        return retrofit.create(DefaultRings::class.java)
    }
    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): CategoriesApi {
        return retrofit.create(CategoriesApi::class.java)
    }

}
