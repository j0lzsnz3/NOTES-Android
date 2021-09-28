package com.snapnoob.notes.di

import com.snapnoob.notes.feature.edit.NotesRepository
import com.snapnoob.notes.feature.edit.NotesRepositoryImpl
import com.snapnoob.notes.feature.login.LoginRepository
import com.snapnoob.notes.feature.login.LoginRepositoryImpl
import com.snapnoob.notes.feature.main.GetAllNotesRepository
import com.snapnoob.notes.feature.main.GetAllNotesRepositoryImpl
import com.snapnoob.notes.feature.profile.UserRepository
import com.snapnoob.notes.feature.profile.UserRepositoryImpl
import com.snapnoob.notes.network.RetrofitService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.snapnoob.notes.feature.register.UserRepository as RegisterUserRepository
import com.snapnoob.notes.feature.register.UserRepositoryImpl as RegisterUserRepositoryImpl

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

    @Singleton
    @Provides
    fun provideProfileUserRepository(retrofitService: RetrofitService): UserRepository {
        return UserRepositoryImpl(retrofitService)
    }

    @Singleton
    @Provides
    fun provideRegisterUserProfile(retrofitService: RetrofitService): RegisterUserRepository {
        return RegisterUserRepositoryImpl(retrofitService)
    }

    @Singleton
    @Provides
    fun provideLoginRepository(retrofitService: RetrofitService): LoginRepository {
        return LoginRepositoryImpl(retrofitService)
    }

}