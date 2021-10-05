package com.snapnoob.notes.network

import com.snapnoob.notes.network.model.GeneralResponse
import com.snapnoob.notes.network.model.PushNotificationRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface NotificationService {
    @POST("notification/topic")
    fun sendNotification(@Body pushNotificationRequest: PushNotificationRequest): Call<GeneralResponse>

    @POST("notification/token")
    fun sendTokenNotification(@Body pushNotificationRequest: PushNotificationRequest): Call<GeneralResponse>

    @POST("notification/data")
    fun sendDataNotification(@Body pushNotificationRequest: PushNotificationRequest): Call<GeneralResponse>
}