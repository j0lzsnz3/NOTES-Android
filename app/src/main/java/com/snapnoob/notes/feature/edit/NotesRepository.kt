package com.snapnoob.notes.feature.edit

import com.snapnoob.notes.network.ResultWrapper
import com.snapnoob.notes.network.RetrofitService
import com.snapnoob.notes.network.isSuccess
import com.snapnoob.notes.network.model.ErrorResponse
import com.snapnoob.notes.network.model.Notes
import com.snapnoob.notes.network.parseErrorResponse
import javax.inject.Inject

interface NotesRepository {
    fun createNotes(notes: Notes): ResultWrapper<Notes>
    fun updateNotes(notes: Notes): ResultWrapper<Notes>
    fun deleteNotes(id: Long): ResultWrapper<String>
}

class NotesRepositoryImpl @Inject constructor(
    private val retrofitService: RetrofitService
): NotesRepository {
    override fun createNotes(notes: Notes): ResultWrapper<Notes> {
        return try {
            val response = retrofitService.getMoviesService().createNotes(notes).execute()

            if (response.isSuccess()) {
                ResultWrapper.Success(response.body()!!)
            } else {
                val errorResponse = response.parseErrorResponse<ErrorResponse>()
                if (errorResponse != null) {
                    ResultWrapper.Failed(errorResponse.status.toString(), errorResponse.error, errorResponse.path)
                } else {
                    ResultWrapper.NetworkError
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ResultWrapper.NetworkError
        }
    }

    override fun updateNotes(notes: Notes): ResultWrapper<Notes> {
        return try {
            val response = retrofitService.getMoviesService().updateNotes(notes).execute()

            if (response.isSuccess()) {
                ResultWrapper.Success(response.body()!!)
            } else {
                val errorResponse = response.parseErrorResponse<ErrorResponse>()
                if (errorResponse != null) {
                    ResultWrapper.Failed(errorResponse.status.toString(), errorResponse.error, errorResponse.path)
                } else {
                    ResultWrapper.NetworkError
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ResultWrapper.NetworkError
        }
    }

    override fun deleteNotes(id: Long): ResultWrapper<String> {
        return try {
            val response = retrofitService.getMoviesService().deleteNotes(id).execute()

            if (response.isSuccess()) {
                ResultWrapper.Success(response.body()!!.message)
            } else {
                val errorResponse = response.parseErrorResponse<ErrorResponse>()
                if (errorResponse != null) {
                    ResultWrapper.Failed(errorResponse.status.toString(), errorResponse.error, errorResponse.path)
                } else {
                    ResultWrapper.NetworkError
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ResultWrapper.NetworkError
        }
    }
}