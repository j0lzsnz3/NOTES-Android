package com.snapnoob.notes.network.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @SerializedName("id")
    val id: Long? = 0,

    @SerializedName("name")
    val name: String,

    @SerializedName("birth_day")
    val birthDay: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("profile_picture")
    val profilePicture: String? = "",

    @SerializedName("password")
    val password: String
): Parcelable