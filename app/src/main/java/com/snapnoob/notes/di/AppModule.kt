package com.snapnoob.notes.di

import android.content.Context
import com.snapnoob.notes.data.SharedPreference
import com.snapnoob.notes.data.SharedPreferenceImpl
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

    @Singleton
    @Provides
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreference {
        return SharedPreferenceImpl(context)
    }
}
