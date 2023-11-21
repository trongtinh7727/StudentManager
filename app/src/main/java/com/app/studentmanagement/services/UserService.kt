package com.app.studentmanagement.services

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("/deleteUser")
    fun deleteUser(@Body request: DeleteUserRequest): Call<Void>
}

data class DeleteUserRequest(val uid: String)
