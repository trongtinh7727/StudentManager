package com.app.studentmanagement.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.studentmanagement.data.models.Account
import com.app.studentmanagement.data.models.Certificate
import com.app.studentmanagement.data.models.Student
import com.app.studentmanagement.data.repository.AccountRepository
import com.app.studentmanagement.data.repository.StudentRepository
import java.time.LocalDateTime
import java.time.ZoneOffset

class StudentViewModel : ViewModel() {
    private val studentRepository = StudentRepository()

    private val _loadingIndicator = MutableLiveData<Boolean>()
    val loadingIndicator: LiveData<Boolean>
        get() = _loadingIndicator


    private val _students = MutableLiveData<List<Student>>()
    val  students: LiveData<List<Student>>
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

}