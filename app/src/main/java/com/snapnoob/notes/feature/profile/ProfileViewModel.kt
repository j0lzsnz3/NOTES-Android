package com.snapnoob.notes.feature.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snapnoob.notes.feature.SingleLiveEvent
import com.snapnoob.notes.network.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val uploadProfilePictureUseCase: UploadProfilePictureUseCase
) : ViewModel() {

    private val eventData = SingleLiveEvent<ProfileEvent>()
    val eventLiveData get() = eventData as LiveData<ProfileEvent>

    fun uploadProfilePicture(requestBody: MultipartBody) {
        viewModelScope.launch {
            uploadProfilePictureUseCase.uploadProfilePicture(requestBody).collect { result ->
                when (result) {
                    is ResultWrapper.Success -> eventData.postValue(ProfileEvent.UpdateProfilePictureSuccess)
                    is ResultWrapper.Failed -> eventData.postValue(ProfileEvent.ShowError(result.message.toString()))
                    is ResultWrapper.NetworkError -> eventData.postValue(ProfileEvent.ShowError("Network Error"))
                }
            }
        }
    }

}

sealed class ProfileEvent {
    object UpdateProfilePictureSuccess : ProfileEvent()
    class ShowError(val error: String) : ProfileEvent()
}