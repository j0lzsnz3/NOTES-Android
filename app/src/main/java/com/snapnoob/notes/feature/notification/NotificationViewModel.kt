package com.snapnoob.notes.feature.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snapnoob.notes.data.SharedPreference
import com.snapnoob.notes.feature.SingleLiveEvent
import com.snapnoob.notes.feature.notification.NotificationType.DATA
import com.snapnoob.notes.feature.notification.NotificationType.TOKEN
import com.snapnoob.notes.feature.notification.NotificationType.TOPIC
import com.snapnoob.notes.network.ResultWrapper
import com.snapnoob.notes.network.model.PushNotificationRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val sendNotificationUseCase: SendNotificationUseCase,
    private val sendTokenNotificationUseCase: SendTokenNotificationUseCase,
    private val sendDataNotificationUseCase: SendDataNotificationUseCase,
    private val sharedPreference: SharedPreference
): ViewModel() {

    private val eventData = SingleLiveEvent<NotificationEvent>()
    val eventLiveData get() = eventData as LiveData<NotificationEvent>

    fun pushNotification(type: NotificationType, request: PushNotificationRequest) {
        viewModelScope.launch {
            when (type) {
                TOPIC -> sendNotificationUseCase.sendNotification(request)
                TOKEN -> sendTokenNotificationUseCase.sendTokenNotification(request)
                DATA -> sendDataNotificationUseCase.sendTokenNotification(request)
            }.collect { result ->
                when (result) {
                    is ResultWrapper.Success -> eventData.postValue(NotificationEvent.PushNotificationSuccess)
                    is ResultWrapper.Failed -> eventData.postValue(NotificationEvent.ShowError(result.message.toString()))
                    is ResultWrapper.NetworkError -> eventData.postValue(NotificationEvent.ShowError("Network Error"))
                }
            }
        }
    }

    fun getToken() {
        val token = sharedPreference.getFcmToken()
        eventData.postValue(NotificationEvent.SetToken(token ?: ""))
    }
}

sealed class NotificationEvent {
    class ShowError(val error: String) : NotificationEvent()
    class SetToken(val token: String) : NotificationEvent()
    object PushNotificationSuccess : NotificationEvent()
}

enum class NotificationType {
    TOPIC, TOKEN, DATA
}