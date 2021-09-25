package com.snapnoob.notes.feature.di

import android.content.Context
import com.snapnoob.notes.network.RetrofitService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRetrofitService(@ApplicationContext context: Context): RetrofitService {
        return RetrofitService(context)
    }
}
