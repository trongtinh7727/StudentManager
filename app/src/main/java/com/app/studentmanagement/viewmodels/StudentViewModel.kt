package com.app.studentmanagement.viewmodels

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.studentmanagement.data.models.Certificate
import com.app.studentmanagement.data.models.Student
import com.app.studentmanagement.data.repository.StudentRepository
import java.io.*

class StudentViewModel : ViewModel() {
    private val studentRepository = StudentRepository()

    private val _loadingIndicator = MutableLiveData<Boolean>()
    val loadingIndicator: LiveData<Boolean>
        get() = _loadingIndicator


    private val _students = MutableLiveData<MutableList<Student>>()
    val  students: LiveData<MutableList<Student>>
        get() = _students

    fun getAllStudent(){
        _loadingIndicator.value = true
        studentRepository.getAllStudents {
            _students.postValue(it)
            _loadingIndicator.postValue(false)
        }
    }

    fun isEmailUnique(email: String,onComplete: (Boolean) -> Unit){
        _loadingIndicator.value = true
        studentRepository.isEmailUnique(email){
            onComplete(it)
            _loadingIndicator.postValue(false)
        }
    }
    fun isCodeUnique(id: String,onComplete: (Boolean) -> Unit){
        _loadingIndicator.value = true
        studentRepository.isCodelUnique(id){
            onComplete(it)
            _loadingIndicator.postValue(false)
        }
    }

    fun search(name: String, studentId: String, classRoom: String){
        _loadingIndicator.value = true
        studentRepository.searchStudents(name,studentId,classRoom) {
            _students.postValue(it)
            _loadingIndicator.postValue(false)
        }
    }

    fun createStudent(student: Student, onComplete: (Boolean)->Unit){
        _loadingIndicator.value = true
        studentRepository.addStudent(student) {
            if(it){
                onComplete(true)
            }else{
                onComplete(false)
            }
            _loadingIndicator.postValue(false)
        }
    }
    fun updateStudent(student: Student, onComplete: (Boolean)->Unit){
        _loadingIndicator.value = true
        studentRepository.updateStudent(student) {
            if(it){
                onComplete(true)
            }else{
                onComplete(false)
            }
            _loadingIndicator.postValue(false)
        }
    }

    fun deleteStudent(id:String, onComplete: (Boolean) -> Unit){
        _loadingIndicator.value = true
        studentRepository.deleteStudent(id) {
            if(it){
                onComplete(true)
            }else{
                onComplete(false)
            }
            _loadingIndicator.postValue(false)
        }
    }

    fun exportCSVFile(uri: Uri, contentResolver: ContentResolver, studentsList: List<Student>): Boolean {
        try {
            val outputStream = contentResolver.openOutputStream(uri)
            val writer = BufferedWriter(OutputStreamWriter(outputStream))

            for (student in studentsList) {
                val studentData = StringBuilder()
                studentData.append("${student.email},${student.fullName},${student.facultyCode},${student.classRoom}")

                for (certificate in student.certificates) {
                    studentData.append(",${certificate.code},${certificate.name}")
                }

                writer.write(studentData.toString())
                writer.newLine()
            }

            writer.close()
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    fun exportCertificatesCSVFile(uri: Uri, contentResolver: ContentResolver, certificates: List<Certificate>): Boolean {
        try {
            val outputStream = contentResolver.openOutputStream(uri)
            val writer = BufferedWriter(OutputStreamWriter(outputStream))
            for (certificate in certificates) {
                val studentData = StringBuilder()
                studentData.append("${certificate.code},${certificate.name}")
                writer.write(studentData.toString())
                writer.newLine()
            }
            writer.close()
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    fun readCertificatesCSVFile(uri: Uri, contentResolver: ContentResolver):MutableList<Certificate>{
        val inputStream = contentResolver.openInputStream(uri)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val certificates = mutableListOf<Certificate>()

        try {
            var line: String? = reader.readLine()
            while (line != null) {
                val tokens = line.split(",")
                if (tokens.size >=2) {
                    val certificateCode = tokens[0]
                    val certificateName = tokens[1]
                    val certificate = Certificate(code = certificateCode, name = certificateName)
                    certificates.add(certificate)

                }
                line = reader.readLine()
            }

            reader.close()
            return certificates
        } catch (e: IOException) {
            e.printStackTrace()
            return mutableListOf()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun readCSVFile(uri: Uri, contentResolver: ContentResolver, facultyMap:MutableMap<String,String>): MutableList<Student> {
        val inputStream = contentResolver.openInputStream(uri)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val studentsList = mutableListOf<Student>() // Create a list to store students

        try {
            var line: String? = reader.readLine()
            while (line != null) {
                val tokens = line.split(",")

                // Check if the line has enough tokens to create a Student object
                if (tokens.size >=3) { // Assuming you need at least 4 columns in the CSV
                    val email = tokens[0]
                    val fullName = tokens[1]
                    val facultyCode = tokens[2]
                    val classRoom = tokens[3]
                    val certificates = mutableListOf<Certificate>()

                    if (tokens.size >= 4){
                        for (i in 4 until tokens.size step 2) {
                            val certificateCode = tokens[i]
                            val certificateName = tokens[i + 1]
                            val certificate = Certificate(code = certificateCode, name = certificateName)
                            certificates.add(certificate)
                        }
                    }
                    val student = facultyMap[facultyCode]?.let {
                        Student(
                            email = email,
                            fullName = fullName,
                            faculty= it,
                            facultyCode = facultyCode,
                            classRoom = classRoom,
                            certificates = certificates
                        )
                    }
                    if (student != null) {
                        studentsList.add(student)
                    }
                }
                line = reader.readLine()
            }

            reader.close()
            return studentsList
        } catch (e: IOException) {
            e.printStackTrace()
            return mutableListOf()
        }
    }

    fun addListStudent(listStudent: MutableList<Student>, onComplete: (Boolean) -> Unit) {
        val studentCount = listStudent.size
        var successCount = 0
        _loadingIndicator.value = true
        for (student in listStudent) {
            studentRepository.addStudent(student) { isSuccess ->
                if (isSuccess) {
                    successCount++
                }
                if (successCount == studentCount) {
                    onComplete(successCount == studentCount)
                    _loadingIndicator.postValue(false)
                }
            }
        }
    }


}