package com.snapnoob.notes.feature.login

import com.snapnoob.notes.network.ResultWrapper
import com.snapnoob.notes.network.RetrofitService
import com.snapnoob.notes.network.isSuccess
import com.snapnoob.notes.network.model.ErrorResponse
import com.snapnoob.notes.network.model.Login
import com.snapnoob.notes.network.model.User
import com.snapnoob.notes.network.parseErrorResponse
import javax.inject.Inject

interface LoginRepository {
    fun login(login: Login): ResultWrapper<User>
}

class LoginRepositoryImpl @Inject constructor(
    private val retrofitService: RetrofitService
): LoginRepository {
    override fun login(login: Login): ResultWrapper<User> {
        return try {
            val response = retrofitService.getUserService().login(login).execute()

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