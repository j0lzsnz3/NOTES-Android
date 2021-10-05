package com.snapnoob.notes.feature.notification

import com.snapnoob.notes.network.ResultWrapper
import com.snapnoob.notes.network.RetrofitService
import com.snapnoob.notes.network.isSuccess
import com.snapnoob.notes.network.model.ErrorResponse
import com.snapnoob.notes.network.model.GeneralResponse
import com.snapnoob.notes.network.model.PushNotificationRequest
import com.snapnoob.notes.network.parseErrorResponse
import javax.inject.Inject

interface NotificationRepository {
    fun sendNotification(request: PushNotificationRequest): ResultWrapper<GeneralResponse>
    fun sendTokenNotification(request: PushNotificationRequest): ResultWrapper<GeneralResponse>
    fun sendDataNotification(request: PushNotificationRequest): ResultWrapper<GeneralResponse>
}

class NotificationRepositoryImpl @Inject constructor(
    private val retrofitService: RetrofitService
): NotificationRepository {

    override fun sendNotification(request: PushNotificationRequest): ResultWrapper<GeneralResponse> {
        return try {
            val response = retrofitService.getNotificationService().sendNotification(request).execute()

            if (response.isSuccess()) {
                ResultWrapper.Success(response.body()!!)
            } else {
                val errorResponse = response.parseErrorResponse<ErrorResponse>()
                if (errorResponse != null) {
                    ResultWrapper.Failed(errorResponse.status.toString(), errorResponse.error, errorResponse.path)
                } else {
                    ResultWrapper.NetworkError
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ResultWrapper.NetworkError
        }
    }

    override fun sendTokenNotification(request: PushNotificationRequest): ResultWrapper<GeneralResponse> {
        return try {
            val response = retrofitService.getNotificationService().sendTokenNotification(request).execute()

            if (response.isSuccess()) {
                ResultWrapper.Success(response.body()!!)
            } else {
                val errorResponse = response.parseErrorResponse<ErrorResponse>()
                if (errorResponse != null) {
                    ResultWrapper.Failed(errorResponse.status.toString(), errorResponse.error, errorResponse.path)
                } else {
                    ResultWrapper.NetworkError
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ResultWrapper.NetworkError
        }
    }

    override fun sendDataNotification(request: PushNotificationRequest): ResultWrapper<GeneralResponse> {
        return try {
            val response = retrofitService.getNotificationService().sendDataNotification(request).execute()

            if (response.isSuccess()) {
                ResultWrapper.Success(response.body()!!)
            } else {
                val errorResponse = response.parseErrorResponse<ErrorResponse>()
                if (errorResponse != null) {
                    ResultWrapper.Failed(errorResponse.status.toString(), errorResponse.error, errorResponse.path)
                } else {
                    ResultWrapper.NetworkError
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ResultWrapper.NetworkError
        }
    }
}