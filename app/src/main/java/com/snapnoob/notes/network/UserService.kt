package com.snapnoob.notes.network

import com.snapnoob.notes.network.model.ChangePassword
import com.snapnoob.notes.network.model.GeneralResponse
import com.snapnoob.notes.network.model.Login
import com.snapnoob.notes.network.model.User
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST

interface UserService {
    @POST("user/register")
    fun register(@Body user: User): Call<User>

    @POST("user/login")
    fun login(@Body login: Login): Call<User>

    @GET("user/profile")
    fun getProfile(@Field("email") email: String): Call<User>

    @POST("user/profile/update")
    fun updateProfile(@Body user: User): Call<User>

    @POST("user/profile/picture")
    fun uploadProfilePicture(@Body requestBody: RequestBody): Call<GeneralResponse>

    @POST("user/password")
    fun updatePassword(@Body changePassword: ChangePassword): Call<User>
}