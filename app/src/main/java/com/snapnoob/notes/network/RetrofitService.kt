package com.snapnoob.notes.network

import android.content.Context
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class RetrofitService @Inject constructor(
    private val context: Context
) {
    private var retrofit: Retrofit? = null

    private fun getRetrofit(): Retrofit {
        if (retrofit == null) {
            val gson = GsonBuilder()
                .setLenient()
                .create()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL_V1)
                .client(createOkHttpFlipper())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit!!
    }

    fun getMoviesService(): NotesService {
        return getRetrofit().create(NotesService::class.java)
    }

    private fun createOkHttpFlipper(): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(
                FlipperOkhttpInterceptor(
                    AndroidFlipperClient.getInstance(context).getPlugin(
                        NetworkFlipperPlugin.ID
                    )
                )
            )
            .build()
    }


    companion object {
        private const val BASE_URL_V1 = "http://10.0.2.2:8080/api/v1/"
    }
}