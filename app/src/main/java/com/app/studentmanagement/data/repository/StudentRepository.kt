package com.app.studentmanagement.data.repository

import com.app.studentmanagement.data.models.Student
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject

class StudentRepository {
    private val db = FirebaseFirestore.getInstance()
    private val studentCollection = db.collection("students")

    fun addStudent(student: Student, onComplete: (Boolean) -> Unit) {
        student.id.takeIf { it.isNotEmpty() }?.let {
            studentCollection.document(it).set(student)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } ?: onComplete(false)
    }

    fun updateStudent(student: Student, onComplete: (Boolean) -> Unit) {
        student.id.takeIf { it.isNotEmpty() }?.let {
            studentCollection.document(it).set(student)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } ?: onComplete(false)
    }

    fun getStudentById(id: String, onComplete: (Student?) -> Unit) {
        studentCollection.document(id).get()
            .addOnSuccessListener { document ->
                val student = document.toObject<Student>()
                onComplete(student)
            }
            .addOnFailureListener { onComplete(null) }
    }

    fun deleteStudent(id: String, onComplete: (Boolean) -> Unit) {
        studentCollection.document(id).delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun getAllStudents(onComplete: (List<Student>) -> Unit) {
        studentCollection.get()
            .addOnSuccessListener { result ->
                val students = result.mapNotNull { it.toObject<Student>() }
                onComplete(students)
            }
            .addOnFailureListener { onComplete(emptyList()) }
    }

    fun searchStudents(name: String, studentId: String, classRoom: String, onComplete: (List<Student>) -> Unit) {
        var query: Query = studentCollection

        if (name.isNotBlank()) {
            query = query.whereEqualTo("name", name)
        }
        if (studentId.isNotBlank()) {
            query = query.whereEqualTo("id", studentId)
        }
        if (classRoom.isNotBlank()) {
            query = query.whereEqualTo("classRoom", classRoom)
        }

        query.get()
            .addOnSuccessListener { result ->
                val students = result.mapNotNull { it.toObject<Student>() }
                onComplete(students)
            }
            .addOnFailureListener { onComplete(emptyList()) }
    }
}
