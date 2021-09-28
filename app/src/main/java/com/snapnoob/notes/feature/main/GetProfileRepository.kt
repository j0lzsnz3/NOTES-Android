package com.snapnoob.notes.feature.main

import com.snapnoob.notes.network.ResultWrapper
import com.snapnoob.notes.network.RetrofitService
import com.snapnoob.notes.network.isSuccess
import com.snapnoob.notes.network.model.ErrorResponse
import com.snapnoob.notes.network.model.User
import com.snapnoob.notes.network.parseErrorResponse
import javax.inject.Inject

interface GetProfileRepository {
    fun getProfile(email: String): ResultWrapper<User>
}

class GetProfileRepositoryImpl @Inject constructor(
    private val retrofitService: RetrofitService
): GetProfileRepository {
    override fun getProfile(email: String): ResultWrapper<User> {
        return try {
            val response = retrofitService.getUserService().getProfile(email).execute()

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