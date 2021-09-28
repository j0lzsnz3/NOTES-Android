package com.snapnoob.notes.network.model

import com.google.gson.annotations.SerializedName

data class ChangePassword(
    @SerializedName("old_password")
    val oldPassword: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("email")
    val email: String
)