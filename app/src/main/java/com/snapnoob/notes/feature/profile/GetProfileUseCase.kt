package com.snapnoob.notes.feature.profile

import com.snapnoob.notes.network.ResultWrapper
import com.snapnoob.notes.network.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

interface GetProfileUseCase {
    fun getProfile(email: String): Flow<ResultWrapper<User>>
}

class GetProfileUseCaseImpl @Inject constructor(
    private val repository: UserRepository,
    private val coroutineContext: CoroutineContext
): GetProfileUseCase {
    override fun getProfile(email: String): Flow<ResultWrapper<User>> {
        return flow { emit(repository.getProfile(email)) }.flowOn(coroutineContext)
    }
}