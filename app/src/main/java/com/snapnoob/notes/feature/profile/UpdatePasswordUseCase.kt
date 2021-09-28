package com.snapnoob.notes.feature.profile

import com.snapnoob.notes.network.ResultWrapper
import com.snapnoob.notes.network.model.ChangePassword
import com.snapnoob.notes.network.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

interface UpdatePasswordUseCase {
    fun updatePassword(changePassword: ChangePassword): Flow<ResultWrapper<User>>
}

class UpdatePasswordUseCaseImpl @Inject constructor(
    private val repository: UserRepository,
    private val coroutineContext: CoroutineContext
): UpdatePasswordUseCase {
    override fun updatePassword(changePassword: ChangePassword): Flow<ResultWrapper<User>> {
        return flow { emit(repository.updatePassword(changePassword)) }.flowOn(coroutineContext)
    }
}