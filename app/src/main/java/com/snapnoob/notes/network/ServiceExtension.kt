package com.snapnoob.notes.network

import com.google.gson.JsonSyntaxException
import okhttp3.Request
import retrofit2.Call
import retrofit2.Response
import okhttp3.Response as OkHttpResponse

fun <T> Response<T>.isSuccess(): Boolean {
    return this.isSuccessful && this.body() != null
}

fun <T> Call<T>.getUrl(): String {
    return (this.request() as Request).url.toString()
}

fun <T> Response<T>.getUrl(): String {
    return (this.raw() as OkHttpResponse).request.url.toString()
}

inline fun <reified T> Response<*>.parseErrorResponse(): T? {
    val errorBody = errorBody()?.string()
    if (!errorBody.isNullOrEmpty()) {
        try {
            return JsonUtil.gsonFromJson(errorBody, T::class.java)
        } catch (jsonSyntaxException: JsonSyntaxException) {
            jsonSyntaxException.printStackTrace()
        }
    }
    return null
}
