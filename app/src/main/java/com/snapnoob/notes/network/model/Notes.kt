package com.snapnoob.notes.network.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notes(
    @SerializedName("id")
    val id: Long? = 0,

    @SerializedName("title")
    val title: String,

    @SerializedName("notes")
    val notes: String
): Parcelable