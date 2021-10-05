package com.snapnoob.notes.feature.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.messaging.FirebaseMessaging
import com.snapnoob.notes.FirebaseSetup
import com.snapnoob.notes.data.SharedPreference
import com.snapnoob.notes.feature.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sharedPreference: SharedPreference
) : ViewModel() {

    private val eventData = SingleLiveEvent<SplashEvent>()
    val eventLiveData get() = eventData as LiveData<SplashEvent>

    fun checkIsUserLoggedIn() {
        if (sharedPreference.getIsLogin()) {
            eventData.postValue(SplashEvent.OpenHomePage)
        } else {
            eventData.postValue(SplashEvent.OpenLoginPage)
        }
    }

    fun setFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FirebaseSetup", "Failed to retrieve Firebase Registration Token", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result ?: ""
            val fcmToken = sharedPreference.getFcmToken()
            if (fcmToken.isNullOrEmpty()) {
                sharedPreference.setFcmToken(token)
            }
        }
    }
}

sealed class SplashEvent {
    object OpenLoginPage : SplashEvent()
    object OpenHomePage : SplashEvent()
}