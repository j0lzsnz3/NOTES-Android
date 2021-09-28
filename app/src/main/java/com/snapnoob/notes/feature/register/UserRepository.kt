package com.snapnoob.notes.feature.register

import com.snapnoob.notes.network.ResultWrapper
import com.snapnoob.notes.network.RetrofitService
import com.snapnoob.notes.network.isSuccess
import com.snapnoob.notes.network.model.ErrorResponse
import com.snapnoob.notes.network.model.GeneralResponse
import com.snapnoob.notes.network.model.User
import com.snapnoob.notes.network.parseErrorResponse
import okhttp3.RequestBody
import javax.inject.Inject

interface UserRepository {
    fun register(user: User): ResultWrapper<User>
    fun uploadProfilePicture(body: RequestBody): ResultWrapper<GeneralResponse>
}

class UserRepositoryImpl @Inject constructor(
    private val retrofitService: RetrofitService
): UserRepository {
    override fun register(user: User): ResultWrapper<User> {
        return try {
            val response = retrofitService.getUserService().register(user).execute()

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

    override fun uploadProfilePicture(body: RequestBody): ResultWrapper<GeneralResponse> {
        return try {
            val response = retrofitService.getUserService().uploadProfilePicture(body).execute()

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