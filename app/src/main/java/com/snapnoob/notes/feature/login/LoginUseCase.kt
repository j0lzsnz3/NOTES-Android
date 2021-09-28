package com.snapnoob.notes.feature.login

import com.snapnoob.notes.network.ResultWrapper
import com.snapnoob.notes.network.model.Login
import com.snapnoob.notes.network.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

interface LoginUseCase {
    fun login(login: Login): Flow<ResultWrapper<User>>
}

class LoginUseCaseImpl @Inject constructor(
    private val repository: LoginRepository,
    private val coroutineContext: CoroutineContext
): LoginUseCase {
    override fun login(login: Login): Flow<ResultWrapper<User>> {
        return flow { emit(repository.login(login)) }.flowOn(coroutineContext)
    }
}