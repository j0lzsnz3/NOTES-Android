package com.snapnoob.notes.feature.biometric

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snapnoob.notes.feature.SingleLiveEvent
import com.snapnoob.notes.feature.login.LoginEvent
import com.snapnoob.notes.feature.login.LoginUseCase
import com.snapnoob.notes.network.ResultWrapper
import com.snapnoob.notes.network.model.Login
import com.snapnoob.notes.network.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnableBiometricLoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val eventData = SingleLiveEvent<EnableBiometricLoginEvent>()
    val eventLiveData get() = eventData as LiveData<EnableBiometricLoginEvent>

    fun login(login: Login) {
        eventData.postValue(EnableBiometricLoginEvent.ShowProgressBar)
        viewModelScope.launch {
            loginUseCase.login(login).collect { result ->
                eventData.postValue(EnableBiometricLoginEvent.DismissProgressBar)
                when (result) {
                    is ResultWrapper.Success      -> {
                        eventData.postValue(EnableBiometricLoginEvent.LoginSuccess(result.value))
                    }
                    is ResultWrapper.Failed       -> {
                        eventData.postValue(EnableBiometricLoginEvent.ShowError(result.message.toString()))
                    }
                    is ResultWrapper.NetworkError -> {
                        eventData.postValue(EnableBiometricLoginEvent.ShowError("Network Error"))
                    }
                }
            }
        }
    }
}

sealed class EnableBiometricLoginEvent {
    object ShowProgressBar : EnableBiometricLoginEvent()
    object DismissProgressBar : EnableBiometricLoginEvent()
    class LoginSuccess(val user: User) : EnableBiometricLoginEvent()
    class ShowError(val error: String) : EnableBiometricLoginEvent()
}