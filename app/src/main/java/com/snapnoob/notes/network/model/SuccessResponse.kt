package com.snapnoob.notes.network.model

import com.google.gson.annotations.SerializedName

data class SuccessResponse<out T>(
    @SerializedName("payload")
    val payload: T
)