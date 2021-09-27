package com.snapnoob.notes.network.model

import com.google.gson.annotations.SerializedName

data class GeneralResponse(
    @SerializedName("message")
    val message: String
)