package com.snapnoob.notes.feature.edit

import com.snapnoob.notes.network.ResultWrapper
import com.snapnoob.notes.network.model.Notes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

interface CreateNotesUseCase {
    fun createNotes(notes: Notes): Flow<ResultWrapper<Notes>>
}

class CreateNotesUseCaseImpl @Inject constructor(
    private val repository: NotesRepository,
    private val coroutineContext: CoroutineContext
): CreateNotesUseCase {
    override fun createNotes(notes: Notes): Flow<ResultWrapper<Notes>> {
        return flow { emit(repository.createNotes(notes)) }.flowOn(coroutineContext)
    }
}