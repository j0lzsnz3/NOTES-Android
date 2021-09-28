package com.snapnoob.notes.feature.profile

import com.snapnoob.notes.network.ResultWrapper
import com.snapnoob.notes.network.RetrofitService
import com.snapnoob.notes.network.isSuccess
import com.snapnoob.notes.network.model.ChangePassword
import com.snapnoob.notes.network.model.ErrorResponse
import com.snapnoob.notes.network.model.GeneralResponse
import com.snapnoob.notes.network.model.User
import com.snapnoob.notes.network.parseErrorResponse
import okhttp3.RequestBody
import javax.inject.Inject

interface UserRepository {
    fun getProfile(email: String): ResultWrapper<User>
    fun updateProfile(user: User): ResultWrapper<User>
    fun uploadProfilePicture(requestBody: RequestBody): ResultWrapper<GeneralResponse>
    fun updatePassword(changePassword: ChangePassword): ResultWrapper<User>
}

class UserRepositoryImpl @Inject constructor(
    private val retrofitService: RetrofitService
): UserRepository {
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

    override fun updateProfile(user: User): ResultWrapper<User> {
        return try {
            val response = retrofitService.getUserService().updateProfile(user).execute()

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

    override fun uploadProfilePicture(requestBody: RequestBody): ResultWrapper<GeneralResponse> {
        return try {
            val response = retrofitService.getUserService().uploadProfilePicture(requestBody).execute()

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

    override fun updatePassword(changePassword: ChangePassword): ResultWrapper<User> {
        return try {
            val response = retrofitService.getUserService().updatePassword(changePassword).execute()

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