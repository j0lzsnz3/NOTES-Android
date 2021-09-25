package com.snapnoob.notes.feature.main

import com.snapnoob.notes.network.ResultWrapper
import com.snapnoob.notes.network.model.Notes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

interface NotesListUseCase {
    fun getAllNotes(): Flow<ResultWrapper<List<Notes>>>
}

class NotesListUseCaseImpl @Inject constructor(
    private val repository: GetAllNotesRepository,
    private val coroutineContext: CoroutineContext
) : NotesListUseCase {
    override fun getAllNotes(): Flow<ResultWrapper<List<Notes>>> {
        return flow { emit(repository.getAllNotes()) }.flowOn(coroutineContext)
    }
}