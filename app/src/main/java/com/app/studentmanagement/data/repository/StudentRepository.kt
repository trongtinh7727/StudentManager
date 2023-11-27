package com.app.studentmanagement.data.repository

import com.app.studentmanagement.data.models.Account
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

        val registration = studentCollection.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                onComplete(emptyList())
                return@addSnapshotListener
            }

            if (querySnapshot != null) {
                val students = mutableListOf<Student>()
                for (document in querySnapshot) {
                    val student = document.toObject(Student::class.java)
                    students.add(student)
                }
                onComplete(students)
            } else {
                onComplete(emptyList())
            }
        }
    }

    fun isEmailUnique(email: String, onComplete: (Boolean) -> Unit) {
        studentCollection.whereEqualTo("email", email).limit(1).get()
            .addOnSuccessListener { documents ->
                onComplete(documents.isEmpty) // If no documents, the email is unique
            }
            .addOnFailureListener {
                onComplete(false) // Handle failure
            }
    }

    fun isCodelUnique(code: String, onComplete: (Boolean) -> Unit) {
        studentCollection.whereEqualTo("id", code).limit(1).get()
            .addOnSuccessListener { documents ->
                onComplete(documents.isEmpty) // If no documents, the email is unique
            }
            .addOnFailureListener {
                onComplete(false) // Handle failure
            }
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
        val registration = query.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                onComplete(emptyList())
                return@addSnapshotListener
            }

            if (querySnapshot != null) {
                val students = mutableListOf<Student>()
                for (document in querySnapshot) {
                    val student = document.toObject(Student::class.java)
                    students.add(student)
                }
                onComplete(students)
            } else {
                onComplete(emptyList())
            }
        }
    }
}
