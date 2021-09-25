package com.snapnoob.notes.feature.main

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
class NotesListViewModel @Inject constructor(
    private val notesListUseCase: NotesListUseCase
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

}

sealed class NotesListEvent {
    class ShowError(val error: String): NotesListEvent()
}