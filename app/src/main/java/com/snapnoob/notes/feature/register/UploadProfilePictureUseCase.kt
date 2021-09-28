package com.snapnoob.notes.feature.register

import com.snapnoob.notes.network.ResultWrapper
import com.snapnoob.notes.network.model.GeneralResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.RequestBody
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

interface UploadProfilePictureUseCase {
    fun uploadProfilePicture(body: RequestBody): Flow<ResultWrapper<GeneralResponse>>
}

class UploadProfilePictureUseCaseImpl @Inject constructor(
    private val repository: UserRepository,
    private val coroutineContext: CoroutineContext
): UploadProfilePictureUseCase {
    override fun uploadProfilePicture(body: RequestBody): Flow<ResultWrapper<GeneralResponse>> {
        return flow { emit(repository.uploadProfilePicture(body)) }.flowOn(coroutineContext)
    }
}