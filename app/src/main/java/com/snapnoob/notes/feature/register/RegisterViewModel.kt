package com.snapnoob.notes.feature.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snapnoob.notes.feature.SingleLiveEvent
import com.snapnoob.notes.network.ResultWrapper
import com.snapnoob.notes.network.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val uploadProfilePictureUseCase: UploadProfilePictureUseCase
) : ViewModel() {
    private val eventData = SingleLiveEvent<RegisterEvent>()
    val eventLiveData get() = eventData as LiveData<RegisterEvent>

    fun register(user: User, isPhotoAvailable: Boolean) {
        eventData.postValue(RegisterEvent.ShowProgressBar)
        viewModelScope.launch {
            registerUseCase.register(user).collect { result ->
                when (result) {
                    is ResultWrapper.Success -> {
                        if (isPhotoAvailable) {
                            eventData.postValue(RegisterEvent.UploadProfilePicture(result.value.id ?: 0L))
                        } else {
                            eventData.postValue(RegisterEvent.RegisterSuccess)
                        }
                    }
                    is ResultWrapper.Failed -> eventData.postValue(RegisterEvent.ShowError(result.message.toString()))
                    is ResultWrapper.NetworkError -> eventData.postValue(RegisterEvent.ShowError("Network Error"))
                }
            }
        }
    }

    fun uploadProfilePicture(requestBody: RequestBody) {
        eventData.postValue(RegisterEvent.ShowProgressBar)
        viewModelScope.launch {
            uploadProfilePictureUseCase.uploadProfilePicture(requestBody).collect { result ->
                when (result) {
                    is ResultWrapper.Success -> {
                        eventData.postValue(RegisterEvent.RegisterSuccess)
                    }
                    is ResultWrapper.Failed -> eventData.postValue(RegisterEvent.ShowError(result.message.toString()))
                    is ResultWrapper.NetworkError -> eventData.postValue(RegisterEvent.ShowError("Network Error"))
                }
            }
        }
    }

}

sealed class RegisterEvent {
    object ShowProgressBar : RegisterEvent()
    object HideProgressBar : RegisterEvent()
    object RegisterSuccess : RegisterEvent()
    class UploadProfilePicture(val userId: Long) : RegisterEvent()
    class ShowError(val error: String) : RegisterEvent()
}