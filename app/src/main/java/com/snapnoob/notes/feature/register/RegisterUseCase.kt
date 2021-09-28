package com.snapnoob.notes.feature.register

import com.snapnoob.notes.network.ResultWrapper
import com.snapnoob.notes.network.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

interface RegisterUseCase {
    fun register(user: User): Flow<ResultWrapper<User>>
}

class RegisterUseCaseImpl @Inject constructor(
    private val repository: UserRepository,
    private val coroutineContext: CoroutineContext
): RegisterUseCase {
    override fun register(user: User): Flow<ResultWrapper<User>> {
        return flow { emit(repository.register(user)) }.flowOn(coroutineContext)
    }
}