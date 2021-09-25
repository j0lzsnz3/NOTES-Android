package com.snapnoob.notes.feature.main

import com.snapnoob.notes.network.ResultWrapper
import com.snapnoob.notes.network.RetrofitService
import com.snapnoob.notes.network.isSuccess
import com.snapnoob.notes.network.model.ErrorResponse
import com.snapnoob.notes.network.model.Notes
import com.snapnoob.notes.network.parseErrorResponse
import javax.inject.Inject

interface GetAllNotesRepository {
    fun getAllNotes(): ResultWrapper<List<Notes>>
}

class GetAllNotesRepositoryImpl @Inject constructor(
    private val retrofitService: RetrofitService
): GetAllNotesRepository {
    override fun getAllNotes(): ResultWrapper<List<Notes>> {
        return try {
            val response = retrofitService.getMoviesService().getAllNotes().execute()

            if (response.isSuccess()) {
                ResultWrapper.Success(response.body()!!.payload)
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