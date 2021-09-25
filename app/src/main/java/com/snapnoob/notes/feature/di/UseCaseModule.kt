package com.snapnoob.notes.feature.di

import com.snapnoob.notes.feature.edit.*
import com.snapnoob.notes.feature.main.GetAllNotesRepository
import com.snapnoob.notes.feature.main.NotesListUseCase
import com.snapnoob.notes.feature.main.NotesListUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Singleton
    @Provides
    fun provideNotesListUseCase(getAllNotesRepository: GetAllNotesRepository): NotesListUseCase {
        return NotesListUseCaseImpl(getAllNotesRepository, Dispatchers.IO)
    }

    @Singleton
    @Provides
    fun provideCreateNotesUseCase(notesRepository: NotesRepository): CreateNotesUseCase {
        return CreateNotesUseCaseImpl(notesRepository, Dispatchers.IO)
    }

    @Singleton
    @Provides
    fun provideUpdateNotesUseCase(notesRepository: NotesRepository): UpdateNotesUseCase {
        return UpdateNotesUseCaseImpl(notesRepository, Dispatchers.IO)
    }

    @Singleton
    @Provides
    fun provideDeleteNotesUseCase(notesRepository: NotesRepository): DeleteNotesUseCase {
        return DeleteNotesUseCaseImpl(notesRepository, Dispatchers.IO)
    }

}