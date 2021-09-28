package com.snapnoob.notes.feature.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snapnoob.notes.data.SharedPreference
import com.snapnoob.notes.feature.SingleLiveEvent
import com.snapnoob.notes.network.ResultWrapper
import com.snapnoob.notes.network.model.Login
import com.snapnoob.notes.network.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val sharedPreference: SharedPreference
) : ViewModel() {

    private val eventData = SingleLiveEvent<LoginEvent>()
    val eventLiveData get() = eventData as LiveData<LoginEvent>

    fun login(login: Login) {
        eventData.postValue(LoginEvent.ShowProgressBar)
        viewModelScope.launch {
            loginUseCase.login(login).collect { result ->
                when (result) {
                    is ResultWrapper.Success -> {
                        saveLoginCredential(result.value.email)
                        eventData.postValue(LoginEvent.LoginSuccess(result.value))
                    }
                    is ResultWrapper.Failed -> eventData.postValue(LoginEvent.ShowError(result.message.toString()))
                    is ResultWrapper.NetworkError -> eventData.postValue(LoginEvent.ShowError("Network Error"))
                }
            }
        }
    }

    private fun saveLoginCredential(email: String) {
        sharedPreference.setIsLogin(true)
        sharedPreference.setUserEmail(email)
    }
}

sealed class LoginEvent {
    object ShowProgressBar : LoginEvent()
    class LoginSuccess(val user: User) : LoginEvent()
    class ShowError(val error: String) : LoginEvent()
}