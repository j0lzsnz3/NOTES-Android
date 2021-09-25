package com.snapnoob.notes.feature.edit

import com.snapnoob.notes.network.ResultWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

interface DeleteNotesUseCase {
    fun deleteNotes(id: Long): Flow<ResultWrapper<String>>
}

class DeleteNotesUseCaseImpl @Inject constructor(
    private val notesRepository: NotesRepository,
    private val coroutineContext: CoroutineContext
): DeleteNotesUseCase {
    override fun deleteNotes(id: Long): Flow<ResultWrapper<String>> {
        return flow { emit(notesRepository.deleteNotes(id)) }.flowOn(coroutineContext)
    }
}