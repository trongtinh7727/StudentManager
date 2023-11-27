package com.app.studentmanagement.data.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.ZoneOffset

data class Student @RequiresApi(Build.VERSION_CODES.O) constructor(
    var id: String = "",
    val email: String = "",
    val fullName: String = "",
    val faculty: String ="",
    val classRoom: String ="",
    val createTime: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+7")),
    var updateTime: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+7")),
    val certificates: MutableList<Certificate> = mutableListOf()
):java.io.Serializable
