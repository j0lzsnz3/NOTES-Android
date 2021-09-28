package com.snapnoob.notes.feature.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snapnoob.notes.data.SharedPreference
import com.snapnoob.notes.feature.SingleLiveEvent
import com.snapnoob.notes.network.ResultWrapper
import com.snapnoob.notes.network.model.Notes
import com.snapnoob.notes.network.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesListViewModel @Inject constructor(
    private val notesListUseCase: NotesListUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val sharedPreference: SharedPreference
) : ViewModel() {

    private val eventData = SingleLiveEvent<NotesListEvent>()
    val eventLiveData get() = eventData as LiveData<NotesListEvent>

    private val notesListData = SingleLiveEvent<List<Notes>>()
    val notesListLiveData get() = notesListData as LiveData<List<Notes>>

    fun getAllNotes() {
        viewModelScope.launch {
            notesListUseCase.getAllNotes().collect { result ->
                when (result) {
                    is ResultWrapper.Success -> {
                        result.value.let { notesListData.postValue(it) }
                    }
                    is ResultWrapper.Failed -> eventData.postValue(NotesListEvent.ShowError(result.message.toString()))
                    is ResultWrapper.NetworkError -> eventData.postValue(NotesListEvent.ShowError("Network Error"))
                }
            }
        }
    }

    fun getProfile() {
        viewModelScope.launch {
            getProfileUseCase.getProfile(sharedPreference.getUserEmail()).collect { result ->
                when (result) {
                    is ResultWrapper.Success -> eventData.postValue(NotesListEvent.DisplayProfile(result.value))
                    is ResultWrapper.Failed -> eventData.postValue(NotesListEvent.ShowError(result.message.toString()))
                    is ResultWrapper.NetworkError -> eventData.postValue(NotesListEvent.ShowError("Network Error"))
                }
            }
        }
    }

}

sealed class NotesListEvent {
    class DisplayProfile(val user: User) : NotesListEvent()
    class ShowError(val error: String) : NotesListEvent()
}