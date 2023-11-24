package com.app.studentmanagement.services

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("/deleteUser")
    fun deleteUser(@Body request: DeleteUserRequest): Call<Void>

    @POST("updateUser")
    fun updateUser(@Body request: UpdateUserRequest): Call<Void>
}

data class DeleteUserRequest(val uid: String)
data class UpdateUserRequest(
    @SerializedName("uid") val uid: String,
    @SerializedName("newEmail") val newEmail: String,
    @SerializedName("newPassword") val newPassword: String

)
