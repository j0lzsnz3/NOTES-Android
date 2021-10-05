package com.snapnoob.notes.feature.notification

import com.snapnoob.notes.network.ResultWrapper
import com.snapnoob.notes.network.model.GeneralResponse
import com.snapnoob.notes.network.model.PushNotificationRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

interface SendTokenNotificationUseCase {
    fun sendTokenNotification(request: PushNotificationRequest): Flow<ResultWrapper<GeneralResponse>>
}

class SendTokenNotificationUseCaseImpl @Inject constructor(
    private val repository: NotificationRepository,
    private val coroutineContext: CoroutineContext
): SendTokenNotificationUseCase {
    override fun sendTokenNotification(request: PushNotificationRequest): Flow<ResultWrapper<GeneralResponse>> =
        flow { emit(repository.sendTokenNotification(request)) }.flowOn(coroutineContext)
}