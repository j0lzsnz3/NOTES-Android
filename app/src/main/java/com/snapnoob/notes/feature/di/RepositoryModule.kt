package com.snapnoob.notes.feature.di

import com.snapnoob.notes.feature.edit.NotesRepository
import com.snapnoob.notes.feature.edit.NotesRepositoryImpl
import com.snapnoob.notes.feature.main.GetAllNotesRepository
import com.snapnoob.notes.feature.main.GetAllNotesRepositoryImpl
import com.snapnoob.notes.network.RetrofitService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideGetAllNotesRepository(retrofitService: RetrofitService): GetAllNotesRepository {
        return GetAllNotesRepositoryImpl(retrofitService)
    }

    @Singleton
    @Provides
    fun provideNotesRepository(retrofitService: RetrofitService): NotesRepository {
        return NotesRepositoryImpl(retrofitService)
    }

}