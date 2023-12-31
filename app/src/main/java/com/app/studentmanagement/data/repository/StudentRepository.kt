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
        val countDocRef = db.collection("facultyCounts").document(student.facultyCode)

        // Transaction to retrieve and increment the count
        db.runTransaction { transaction ->
            val snapshot = transaction.get(countDocRef)
            val currentCount = snapshot.getLong("count") ?: 0
            val newCount = currentCount + 1
            transaction.set(countDocRef, mapOf("count" to newCount))

            newCount
        }.addOnSuccessListener { newCount ->
            // Create a new student ID using the faculty code and new count
            val studentId = "${student.facultyCode}$newCount"
            student.id = studentId

            studentCollection.document(studentId).set(student)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        }.addOnFailureListener {
            onComplete(false)
        }
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

    fun getAllStudents(onComplete: (MutableList<Student>) -> Unit) {

        val registration = studentCollection.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                onComplete(mutableListOf())
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
                onComplete(mutableListOf())
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

    fun searchStudents(name: String, studentId: String, classRoom: String, onComplete: (MutableList<Student>) -> Unit) {
        val query: Query = studentCollection

        query.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                onComplete(mutableListOf())
                return@addSnapshotListener
            }

            if (querySnapshot != null) {
                val students = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Student::class.java)
                }.filter { student ->
                    // Apply client-side filters
                    (name.isBlank() || student.fullName.contains(name, ignoreCase = true)) &&
                            (studentId.isBlank() || student.id.contains(studentId, ignoreCase = true)) &&
                            (classRoom.isBlank() || student.classRoom.contains(classRoom, ignoreCase = true))
                }
                onComplete(students as MutableList<Student>)
            } else {
                onComplete(mutableListOf())
            }
        }
    }

}
