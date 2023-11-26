package com.app.studentmanagement.data.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.ZoneOffset

data class Account @RequiresApi(Build.VERSION_CODES.O) constructor(
    var uid: String ="",
    val id: String ="",
    val name: String ="",
    val email: String = "",
    val avatarUrl: String = "https://cdn-icons-png.flaticon.com/512/3607/3607444.png",
    val role: Role = Role.Employee,
    val createTime: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+7")),
    var updateTime: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+7"))
    ):java.io.Serializable