package com.snapnoob.notes.di

import com.snapnoob.notes.feature.edit.*
import com.snapnoob.notes.feature.login.LoginRepository
import com.snapnoob.notes.feature.login.LoginUseCase
import com.snapnoob.notes.feature.login.LoginUseCaseImpl
import com.snapnoob.notes.feature.main.GetAllNotesRepository
import com.snapnoob.notes.feature.main.GetProfileRepository
import com.snapnoob.notes.feature.main.NotesListUseCase
import com.snapnoob.notes.feature.main.NotesListUseCaseImpl
import com.snapnoob.notes.feature.profile.*
import com.snapnoob.notes.feature.register.RegisterUseCase
import com.snapnoob.notes.feature.register.RegisterUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton
import com.snapnoob.notes.feature.register.UploadProfilePictureUseCase as RegisterUploadProfilePictureUseCase
import com.snapnoob.notes.feature.register.UploadProfilePictureUseCaseImpl as RegisterUploadProfilePictureUseCaseImpl
import com.snapnoob.notes.feature.register.UserRepository as RegisterUserRepository
import com.snapnoob.notes.feature.main.GetProfileUseCase as MainGetProfileUseCase
import com.snapnoob.notes.feature.main.GetProfileUseCaseImpl as MainGetProfileUseCaseImpl

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

    @Singleton
    @Provides
    fun provideLoginUseCase(loginRepository: LoginRepository): LoginUseCase {
        return LoginUseCaseImpl(loginRepository, Dispatchers.IO)
    }

    @Singleton
    @Provides
    fun provideRegisterUseCase(registerUserRepository: RegisterUserRepository): RegisterUseCase {
        return RegisterUseCaseImpl(registerUserRepository, Dispatchers.IO)
    }

    @Singleton
    @Provides
    fun provideRegisterUploadProfilePictureUseCase(registerUserRepository: RegisterUserRepository): RegisterUploadProfilePictureUseCase {
        return RegisterUploadProfilePictureUseCaseImpl(registerUserRepository, Dispatchers.IO)
    }

    @Singleton
    @Provides
    fun provideProfileUploadProfilePictureUseCase(userRepository: UserRepository): UploadProfilePictureUseCase {
        return UploadProfilePictureUseCaseImpl(userRepository, Dispatchers.IO)
    }

    @Singleton
    @Provides
    fun provideUpdatePasswordUseCase(userRepository: UserRepository): UpdatePasswordUseCase {
        return UpdatePasswordUseCaseImpl(userRepository, Dispatchers.IO)
    }

    @Singleton
    @Provides
    fun provideUpdateProfileUseCase(userRepository: UserRepository): UpdateProfileUseCase {
        return UpdateProfileUseCaseImpl(userRepository, Dispatchers.IO)
    }

    @Singleton
    @Provides
    fun provideGetProfileUseCase(userRepository: UserRepository): GetProfileUseCase {
        return GetProfileUseCaseImpl(userRepository, Dispatchers.IO)
    }

    @Singleton
    @Provides
    fun provideMainGetProfileUseCase(getProfileRepository: GetProfileRepository): MainGetProfileUseCase {
        return MainGetProfileUseCaseImpl(getProfileRepository, Dispatchers.IO)
    }

}