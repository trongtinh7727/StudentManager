package com.app.studentmanagement.data.repository

import com.app.studentmanagement.data.models.Account
import com.app.studentmanagement.data.models.Role
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class AccountRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun addAccount(account: Account, onComplete: (Boolean) -> Unit) {
        val userCollection = db.collection("accounts")
        account.id?.let {
            userCollection.document(it).set(account)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener { e ->
                onComplete(false)
            }
        }
    }

    fun getAccountByRole(role: Role, onComplete: (List<Account>) -> Unit){
        val accountCollection = db.collection("accounts")

        val query = if (role == Role.All) {
            accountCollection
        } else {
            accountCollection.whereEqualTo("role", role)
        }

        val registration = query.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                onComplete(emptyList())
                return@addSnapshotListener
            }

            if (querySnapshot != null) {
                val accounts = mutableListOf<Account>()
                for (document in querySnapshot) {
                    val account = document.toObject(Account::class.java)
                    accounts.add(account)
                }
                onComplete(accounts)
            } else {
                onComplete(emptyList())
            }
        }
    }

    fun updateAccount(account: Account, onComplete: (Boolean) -> Unit) {
        val userCollection = db.collection("accounts")
        account.id?.let {
            userCollection.document(it).set(account, SetOptions.merge())
                .addOnSuccessListener {
                    onComplete(true)
                }
                .addOnFailureListener {
                    onComplete(false)
                }
        }
    }

    fun deleteAccount(accountId: String, onComplete: (Boolean) -> Unit) {
        // First, delete the user from Firebase Authentication

    }
}