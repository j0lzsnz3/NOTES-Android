package com.snapnoob.notes.feature.profile

import com.snapnoob.notes.network.ResultWrapper
import com.snapnoob.notes.network.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

interface UpdateProfileUseCase {
    fun updateProfile(user: User): Flow<ResultWrapper<User>>
}

class UpdateProfileUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository,
    private val coroutineContext: CoroutineContext
): UpdateProfileUseCase {
    override fun updateProfile(user: User): Flow<ResultWrapper<User>> {
        return flow { emit(userRepository.updateProfile(user)) }.flowOn(coroutineContext)
    }
}