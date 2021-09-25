package com.snapnoob.notes.network

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()

    data class Failed(
        val code: String?,
        val message: String?,
        val url: String
    ) : ResultWrapper<Nothing>()

    object NetworkError : ResultWrapper<Nothing>()
}