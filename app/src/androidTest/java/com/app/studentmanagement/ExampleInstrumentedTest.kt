package com.app.studentmanagement

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app.studentmanagement.data.models.Account
import com.app.studentmanagement.data.models.Role
import com.app.studentmanagement.data.models.Student
import com.app.studentmanagement.data.repository.AccountRepository
import com.app.studentmanagement.data.repository.StudentRepository
import com.google.firebase.auth.FirebaseAuth

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    val accountRepository = AccountRepository()
    val studentRepository = StudentRepository()
    // Context of the app under test.
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    // Get the faculties and facultiesCode arrays from resources
    val faculties = appContext.resources.getStringArray(R.array.faculties)
    val facultiesCode = appContext.resources.getStringArray(R.array.facultiesCode)
    @Test
    fun useAppContext() {
        // Context of the app under test.
        accountRepository.deleteUser("Ju48UYrGFaZlZHrBNmW2APlhskz1"){
            assert(it)
        }
        Thread.sleep(5000)
    }

    @Test
    @Throws(Exception::class)
    fun testDeteleAcount(){
        accountRepository.deleteUser("zXNqmukSHgYUm105wSkAdk7qhMO2"){
            assert(it)
        }
        Thread.sleep(1000)

    }
    fun generateRandomName(i: Int): String {
        val names = listOf(
            "Đặng Quang Hà",
            "Phạm Trung Linh",
            "Trần Thành Khoa",
            "Trần Tâm Dương",
            "Đặng Văn Linh",
            "Đặng Tâm Linh",
            "Vũ Hữu Cường",
            "Hoàng Quang Hà",
            "Vũ Ngọc Khoa",
            "Lê Văn Cường",
            "Vũ Hữu Khoa",
            "Vũ Trung Nhung",
            "Vũ Tâm Dương",
            "Huỳnh  Bảo",
            "Vũ Thành Minh",
            "Trần Trung Minh",
            "Trần  Minh",
            "Huỳnh Trung Linh",
            "Huỳnh  Anh",
            "Huỳnh Thị Cường",
            "Đặng Tâm Bảo",
            "Huỳnh Ngọc Linh",
            "Võ  Minh",
            "Phạm Hữu Bảo",
            "Huỳnh Tâm Khoa",
            "Phạm  Minh",
            "Hoàng Tâm Khoa",
            "Vũ  Phương",
            "Phan Hữu Bảo",
            "Nguyễn Ngọc Minh"
        )
        return names[i]
    }


    // Function to generate a random role
    fun generateRandomRole(): Role {
        val roles = arrayOf(
            Role.Manager,
            Role.Employee,
            Role.Admin
        )
        return roles.random()
    }
    @Test
    fun createTwentyAccounts() {
        val accountsToCreate = 29
        val createdAccounts = mutableListOf<Boolean>()

        // Create 20 accounts
        for (i in 1..accountsToCreate) {
            val emailValue = "account$i@app.com"
            val randomName = generateRandomName(i)
            val randomRole = generateRandomRole()
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailValue, "admin123")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = task.result?.user
                        if (firebaseUser != null) {
                            // Now, you can add the user's information to Firestore as an "account"
                            val account = Account(
                                uid = firebaseUser.uid, // Use the user's UID as the account ID
                                name = randomName, // Replace with the user's name
                                id = "acc$i",
                                email = emailValue,
                                role = randomRole
                            )

                            // Add the account to Firestore
                            accountRepository.addAccount(account) { success ->
                                if (success) {
                                    // Account creation in Firestore was successful
                                    createdAccounts.add(success)                                } else {
                                    // Handle the case where adding the account to Firestore failed
                                }
                            }
                        }
                    } else {
                        val exception = task.exception
                        if (exception != null) {
                        }
                    }
                }
            Thread.sleep(1000)
        }

        // Wait for the accounts to be created (adjust the delay as needed)
        Thread.sleep(1000)

        // Check if all accounts were successfully created
        assert(createdAccounts.all { it })
    }

    @Test
    fun createStudet(){
        val accountsToCreate = 29

        // Create 20 accounts
        for (i in 1..accountsToCreate) {
            val emailValue = "student$i@app.com"
            val randomName = generateRandomName(i)
            val randomIndex = Random.nextInt(faculties.size)
            val randomFaculty = faculties[randomIndex]
            val randomFacultyCode = facultiesCode[randomIndex]


            val student = Student(
                email = emailValue,
                fullName = randomName,
                classRoom = "210$randomFacultyCode",
                faculty = randomFaculty,
                facultyCode = randomFacultyCode
            )

            studentRepository.addStudent(student){
                Log.i("Test", "createStudet: ${student.fullName}")
            }
            Thread.sleep(1000)
        }

    }
}