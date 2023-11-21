package com.app.studentmanagement.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.app.studentmanagement.data.models.Certificate
import com.app.studentmanagement.data.models.Student
import java.time.LocalDateTime
import java.time.ZoneOffset

class StudentViewModel : ViewModel() {
    @RequiresApi(Build.VERSION_CODES.O)
    fun generateStudents(count: Int): List<Student> {
        val students = mutableListOf<Student>()

        repeat(count) {
            val id = "ID-${(1000..9999).random()}"
            val email = "student${(1..100).random()}@example.com"
            val fullName = "Vox Trongj Tong`"
            val faculty = "Faculty-${(1..5).random()}"
            val classRoom = "Class-${(101..200).random()}"
            val createTime = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+7"))
            val certificates = generateCertificates()

            students.add(
                Student(
                    id,
                    email,
                    faculty,
                    fullName,
                    classRoom,
                    createTime,
                    createTime,
                    certificates
                ))
        }

        return students
    }

    private fun generateCertificates(): List<Certificate> {
        val numCertificates = (0..5).random() // Generate a random number of certificates (0 to 5)
        val certificates = mutableListOf<Certificate>()

        repeat(numCertificates) {
            val certificateCode = "Code-${(1..10).random()}"
            val certificateName = "Certificate-${(1..10).random()}"
            certificates.add(Certificate(certificateCode, certificateName))
        }

        return certificates
    }
}