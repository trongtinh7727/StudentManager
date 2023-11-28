package com.app.studentmanagement.data.models

import java.time.LocalDateTime
import java.time.ZoneOffset

data class LoginHistory(
    var device: String = "",
    val time: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+7")),
)
