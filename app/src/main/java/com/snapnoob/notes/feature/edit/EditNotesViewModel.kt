package com.snapnoob.notes.feature.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snapnoob.notes.feature.SingleLiveEvent
import com.snapnoob.notes.network.ResultWrapper
import com.snapnoob.notes.network.model.Notes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditNotesViewModel @Inject constructor(
    private val createNotesUseCase: CreateNotesUseCase,
    private val updateNotesUseCase: UpdateNotesUseCase,
    private val deleteNotesUseCase: DeleteNotesUseCase
): ViewModel() {

    private val eventData = SingleLiveEvent<EditNotesEvent>()
    val eventLiveData get() = eventData as LiveData<EditNotesEvent>

    private val notesData = SingleLiveEvent<Notes>()
    val notesLiveData get() = notesData as LiveData<Notes>

    fun createNotes(notes: Notes) {
        viewModelScope.launch {
            createNotesUseCase.createNotes(notes).collect { result ->
                when (result) {
                    is ResultWrapper.Success -> eventData.postValue(EditNotesEvent.ProcessSuccess)
                    is ResultWrapper.Failed -> eventData.postValue(EditNotesEvent.ShowError(result.message.toString()))
                    is ResultWrapper.NetworkError -> eventData.postValue(EditNotesEvent.ShowError("Network Error"))
                }
            }
        }
    }

    fun updateNotes(notes: Notes) {
        viewModelScope.launch {
            updateNotesUseCase.updateNotes(notes).collect { result ->
                when (result) {
                    is ResultWrapper.Success -> eventData.postValue(EditNotesEvent.ProcessSuccess)
                    is ResultWrapper.Failed -> eventData.postValue(EditNotesEvent.ShowError(result.message.toString()))
                    is ResultWrapper.NetworkError -> eventData.postValue(EditNotesEvent.ShowError("Network Error"))
                }

            }
        }
    }

    fun deleteNotes(id: Long) {
        viewModelScope.launch {
            deleteNotesUseCase.deleteNotes(id).collect { result ->
                when (result) {
                    is ResultWrapper.Success -> eventData.postValue(EditNotesEvent.ProcessSuccess)
                    is ResultWrapper.Failed -> eventData.postValue(EditNotesEvent.ShowError(result.message.toString()))
                    is ResultWrapper.NetworkError -> eventData.postValue(EditNotesEvent.ShowError("Network Error"))
                } }
        }
    }

}

sealed class EditNotesEvent {
    object ProcessSuccess : EditNotesEvent()
    class ShowError(val error: String) : EditNotesEvent()
}