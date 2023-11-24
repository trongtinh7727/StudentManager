package com.app.studentmanagement

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app.studentmanagement.data.models.Account
import com.app.studentmanagement.data.models.Role
import com.app.studentmanagement.data.repository.AccountRepository
import com.google.firebase.auth.FirebaseAuth

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    val accountRepository = AccountRepository()
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
            "Clarance Corzon",
            "Kathye Monte",
            "Hilde Eveque",
            "Ailis Karle",
            "Sumner Drennan",
            "Beauregard Morecomb",
            "Milissent Dinnington",
            "Vickie Wrixon",
            "Rorie Wanden",
            "Ethelind Rickerby",
            "Elsa Clixby",
            "Ranice Brewitt",
            "Dickie Ellacott",
            "Llewellyn Harmes",
            "Aeriell Volcker",
            "Klarika Pantone",
            "Michaelina Zotto",
            "Melamie Barcroft",
            "Burton Ragot",
            "Catherin Paulitschke",
            "Rafaellle Trail",
            "Joyan Fitzmaurice",
            "Jody Whitlaw",
            "Gualterio Harnetty",
            "Michail Pandya",
            "Trumann Tidswell",
            "Penny Trubshaw",
            "Aimil Shurrock",
            "Clevie Ellesworthe",
            "Dunc Kinig"
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

}