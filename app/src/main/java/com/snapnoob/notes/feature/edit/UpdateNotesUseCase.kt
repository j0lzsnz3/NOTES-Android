package com.snapnoob.notes.feature.edit

import com.snapnoob.notes.network.ResultWrapper
import com.snapnoob.notes.network.model.Notes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

interface UpdateNotesUseCase {
    fun updateNotes(notes: Notes): Flow<ResultWrapper<Notes>>
}

class UpdateNotesUseCaseImpl @Inject constructor(
    private val repository: NotesRepository,
    private val coroutineContext: CoroutineContext
) : UpdateNotesUseCase {
    override fun updateNotes(notes: Notes): Flow<ResultWrapper<Notes>> {
        return flow { emit(repository.updateNotes(notes)) }.flowOn(coroutineContext)
    }
}