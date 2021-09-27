package com.snapnoob.notes.network

import com.snapnoob.notes.network.model.GeneralResponse
import com.snapnoob.notes.network.model.Notes
import com.snapnoob.notes.network.model.SuccessResponse
import retrofit2.Call
import retrofit2.http.*

interface NotesService {
    @POST("notes/create")
    fun createNotes(@Body notes: Notes): Call<Notes>

    @POST("notes/update")
    fun updateNotes(@Body notes: Notes): Call<Notes>

    @GET("notes/all")
    fun getAllNotes(): Call<List<Notes>>

    @FormUrlEncoded
    @POST("notes/delete")
    fun deleteNotes(@Field("notes_id") id: Long): Call<GeneralResponse>

}