package com.snapnoob.notes.network.model

import com.google.gson.annotations.SerializedName

data class PushNotificationRequest(
    @SerializedName("title")
    val title: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("topic")
    val topic: String,

    @SerializedName("token")
    val token: String
)