package com.snapnoob.notes.feature.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
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
}

sealed class SplashEvent {
    object OpenLoginPage : SplashEvent()
    object OpenHomePage : SplashEvent()
}