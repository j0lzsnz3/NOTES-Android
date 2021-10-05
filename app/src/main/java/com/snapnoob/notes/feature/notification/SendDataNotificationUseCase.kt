package com.snapnoob.notes.feature.notification

import com.snapnoob.notes.network.ResultWrapper
import com.snapnoob.notes.network.model.GeneralResponse
import com.snapnoob.notes.network.model.PushNotificationRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

interface SendDataNotificationUseCase {
    fun sendTokenNotification(request: PushNotificationRequest): Flow<ResultWrapper<GeneralResponse>>
}

class SendDataNotificationUseCaseImpl @Inject constructor(
    private val repository: NotificationRepository,
    private val coroutineContext: CoroutineContext
): SendDataNotificationUseCase {
    override fun sendTokenNotification(request: PushNotificationRequest): Flow<ResultWrapper<GeneralResponse>> =
        flow { emit(repository.sendDataNotification(request)) }.flowOn(coroutineContext)
}